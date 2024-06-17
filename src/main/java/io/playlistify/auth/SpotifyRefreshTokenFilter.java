package io.playlistify.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.playlistify.utils.EncryptionUtils;
import io.playlistify.web.CookieHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SpotifyRefreshTokenFilter extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;
  private final EncryptionUtils encryptionUtils;
  private final SpotifyAuthService spotifyAuthService;
  private final CookieHandler cookieHandler;

  public SpotifyRefreshTokenFilter(
      final ObjectMapper objectMapper,
      final EncryptionUtils encryptionUtils,
      final SpotifyAuthService spotifyAuthService,
      final CookieHandler cookieHandler) {
    this.objectMapper = objectMapper;
    this.encryptionUtils = encryptionUtils;
    this.spotifyAuthService = spotifyAuthService;
    this.cookieHandler = cookieHandler;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final var cookies = request.getCookies();
    if (cookies == null) {
      filterChain.doFilter(request, response);
      return;
    }
    final var first =
        Arrays.stream(cookies).filter(cookie -> "tk".equals(cookie.getName())).findFirst();
    if (first.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }
    final var cookie = first.get();
    final var cookieValue = cookie.getValue();
    if (cookieValue == null || cookieValue.isBlank()) {
      this.cookieHandler.clearCookie(cookie, response);
      filterChain.doFilter(request, response);
      return;
    }
    try {
      final var cookieJsonValue = this.encryptionUtils.decrypt(cookieValue);
      final var spotifyAuthCookie =
          this.objectMapper.readValue(cookieJsonValue, SpotifyAuthCookie.class);
      if (spotifyAuthCookie.isTokenExpired()) {
        if (spotifyAuthCookie.refreshTokenIsBlank()) {
          this.cookieHandler.clearCookie(cookie, response);
          filterChain.doFilter(request, response);
          return;
        }
        final var tokens =
            this.spotifyAuthService.refreshAccessToken(spotifyAuthCookie.refreshToken());
        final var refreshedTkCookie = this.cookieHandler.createAuthCookie(tokens);
        response.addHeader("Set-Cookie", refreshedTkCookie.toString());
      }
      request.setAttribute("authData", new AuthData(true, spotifyAuthCookie.accessToken()));
    } catch (final RuntimeException e) {
      this.cookieHandler.clearCookie(cookie, response);
    } finally {
      filterChain.doFilter(request, response);
    }
  }

  record SpotifyAuthCookie(
      @JsonProperty("access_token") String accessToken,
      @JsonProperty("expires_in") int expiresIn,
      @JsonProperty("token_type") String tokenType,
      @JsonProperty("refresh_token") String refreshToken,
      @JsonProperty("invalid_after") Instant invalidAfter) {

    boolean isTokenExpired() {
      return Instant.now(Clock.systemUTC()).isAfter(invalidAfter());
    }

    boolean refreshTokenNotBlank() {
      return refreshToken() != null && !refreshToken().isBlank();
    }

    boolean refreshTokenIsBlank() {
      return !refreshTokenNotBlank();
    }
  }
}
