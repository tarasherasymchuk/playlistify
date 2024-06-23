package io.playlistify.middleware;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.playlistify.auth.AuthData;
import io.playlistify.auth.SpotifyOAuth2Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

class SpotifyMiddlewareTest {

  private SpotifyOAuth2Properties properties;
  private RestTemplate restTemplate;
  private SpotifyMiddleware spotifyMiddleware;
  private RequestAttributes requestAttributes;

  @BeforeEach
  void setUp() {
    properties = mock(SpotifyOAuth2Properties.class);
    restTemplate = mock(RestTemplate.class);
    spotifyMiddleware = new SpotifyMiddleware(properties, restTemplate);
    requestAttributes = mock(RequestAttributes.class);
    RequestContextHolder.setRequestAttributes(requestAttributes);
  }

  @Test
  void testDoGet_Authenticated() {
    final String path = "/me";
    final Class<String> responseType = String.class;
    final Map<String, Object> params = Map.of("key", "value");
    final AuthData authData = new AuthData(true, "access_token");

    when(requestAttributes.getAttribute("authData", RequestAttributes.SCOPE_REQUEST)).thenReturn(authData);
    when(properties.apiUri()).thenReturn("https://api.spotify.com");

    final ResponseEntity<String> responseEntity = new ResponseEntity<>("response", HttpStatus.OK);
    when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(responseType), eq(params)))
            .thenReturn(responseEntity);

    final String result = spotifyMiddleware.doGet(path, responseType, params);

    assertEquals("response", result);

    final ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    verify(restTemplate).exchange(eq("https://api.spotify.com/v1/me?key={key}"), eq(HttpMethod.GET), entityCaptor.capture(), eq(responseType), eq(params));
    final HttpEntity capturedEntity = entityCaptor.getValue();
    assertNotNull(capturedEntity);
    assertTrue(capturedEntity.getHeaders().containsKey(HttpHeaders.AUTHORIZATION));
    assertEquals("Bearer access_token", capturedEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void testDoGet_Unauthenticated() {
    final String path = "/me";
    final Class<String> responseType = String.class;

    when(requestAttributes.getAttribute("authData", RequestAttributes.SCOPE_REQUEST)).thenReturn(null);

    assertThrows(UnauthenticatedException.class, () -> spotifyMiddleware.doGet(path, responseType));
  }
}