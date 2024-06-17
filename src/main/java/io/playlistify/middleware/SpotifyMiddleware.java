package io.playlistify.middleware;

import io.playlistify.auth.AuthData;
import io.playlistify.auth.SpotifyOAuth2Properties;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SpotifyMiddleware {

  private static final int REQUEST_SCOPE = 0;
  private final SpotifyOAuth2Properties properties;
  private final RestTemplate restTemplate;

  public SpotifyMiddleware(
      final SpotifyOAuth2Properties properties, final RestTemplate restTemplate) {
    this.properties = properties;
    this.restTemplate = restTemplate;
  }

  public <T> T doGet(final String path, final Class<T> type) {
    return doGet(path, type, Map.of());
  }

  public <T> T doGet(final String path, final Class<T> type, final Map<String, Object> params) {
    final var uriComponentsBuilder = UriComponentsBuilder.fromPath(path);
    params.keySet().forEach(key -> uriComponentsBuilder.queryParam(key, "{" + key + "}"));
    final var authData =
        (AuthData)
            RequestContextHolder.currentRequestAttributes().getAttribute("authData", REQUEST_SCOPE);
    if (authData == null || !authData.authenticated()) {
      throw new UnauthenticatedException("authData is null");
    }
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(authData.accessToken());
    final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
    try {
      final var responseEntity =
          this.restTemplate.exchange(
              this.properties.apiUri() + "/v1" + uriComponentsBuilder.encode().toUriString(),
              HttpMethod.GET,
              entity,
              type,
              params);
      return responseEntity.getBody();
    } catch (final RestClientException r) {
      throw new UnauthenticatedException(r.getMessage());
    }
  }
}
