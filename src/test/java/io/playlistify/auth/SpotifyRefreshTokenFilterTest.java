package io.playlistify.auth;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.playlistify.utils.EncryptionUtils;
import io.playlistify.web.CookieHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SpotifyRefreshTokenFilterTest {

  private ObjectMapper objectMapper;
  private EncryptionUtils encryptionUtils;
  private SpotifyAuthService spotifyAuthService;
  private CookieHandler cookieHandler;
  private SpotifyRefreshTokenFilter filter;

  @BeforeEach
  public void setUp() {
    objectMapper = mock(ObjectMapper.class);
    encryptionUtils = mock(EncryptionUtils.class);
    spotifyAuthService = mock(SpotifyAuthService.class);
    cookieHandler = mock(CookieHandler.class);
    filter = new SpotifyRefreshTokenFilter(objectMapper, encryptionUtils, spotifyAuthService, cookieHandler);
  }

  @Test
  void testDoFilterInternal_NoCookies() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final FilterChain filterChain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_NoTkCookie() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie("other", "value"));
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final FilterChain filterChain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_EmptyTkCookie() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(new Cookie("tk", ""));
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final FilterChain filterChain = mock(FilterChain.class);

    filter.doFilterInternal(request, response, filterChain);

    verify(cookieHandler).clearCookie(any(Cookie.class), eq(response));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_ExpiredToken() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final HttpServletResponse response = mock(HttpServletResponse.class);
    final FilterChain filterChain = mock(FilterChain.class);
    final Cookie tkCookie = new Cookie("tk", "encryptedValue");
    request.setCookies(tkCookie);

    final SpotifyRefreshTokenFilter.SpotifyAuthCookie authCookie = new SpotifyRefreshTokenFilter.SpotifyAuthCookie(
            "access_token", 3600, "Bearer", "refresh_token", Instant.now(Clock.systemUTC()).minusSeconds(10));
    when(encryptionUtils.decrypt("encryptedValue")).thenReturn("decryptedValue");
    when(objectMapper.readValue("decryptedValue", SpotifyRefreshTokenFilter.SpotifyAuthCookie.class)).thenReturn(authCookie);
    when(spotifyAuthService.refreshAccessToken("refresh_token")).thenReturn(
            "{\"access_token\":\"new_access_token\",\"expires_in\":3600,\"token_type\":\"Bearer\",\"refresh_token\":\"new_refresh_token\"}"
    );

    filter.doFilterInternal(request, response, filterChain);

    verify(cookieHandler).createAuthCookie(any(String.class));
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void testDoFilterInternal_ValidToken() throws Exception {
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();
    final FilterChain filterChain = mock(FilterChain.class);
    final Cookie tkCookie = new Cookie("tk", "encryptedValue");
    request.setCookies(tkCookie);

    final SpotifyRefreshTokenFilter.SpotifyAuthCookie authCookie = new SpotifyRefreshTokenFilter.SpotifyAuthCookie(
            "access_token", 3600, "Bearer", "refresh_token", Instant.now(Clock.systemUTC()).plusSeconds(3600));
    when(encryptionUtils.decrypt("encryptedValue")).thenReturn("decryptedValue");
    when(objectMapper.readValue("decryptedValue", SpotifyRefreshTokenFilter.SpotifyAuthCookie.class)).thenReturn(authCookie);

    filter.doFilterInternal(request, response, filterChain);

    final AuthData authData = (AuthData) request.getAttribute("authData");
    assertNotNull(authData);
    assertEquals("access_token", authData.accessToken());
    verify(filterChain).doFilter(request, response);
  }
}