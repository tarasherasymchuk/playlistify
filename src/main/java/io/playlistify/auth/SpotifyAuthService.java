package io.playlistify.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.playlistify.utils.EncryptionUtils;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.function.Function;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
class SpotifyAuthService {

  private final RestTemplate restTemplate;
  private final SpotifyOAuth2Properties spotifyOAuth2Properties;
  private final EncryptionUtils encryptionUtils;
  private final ObjectMapper objectMapper;

  public SpotifyAuthService(
      final RestTemplate restTemplate,
      final SpotifyOAuth2Properties spotifyOAuth2Properties,
      final EncryptionUtils encryptionUtils,
      final ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.spotifyOAuth2Properties = spotifyOAuth2Properties;
    this.encryptionUtils = encryptionUtils;
    this.objectMapper = objectMapper;
  }

  public String requestToken(final String code) {
    final MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
    formParams.add("code", code);
    formParams.add("scope", this.spotifyOAuth2Properties.scope());
    formParams.add("grant_type", "authorization_code");
    formParams.add("redirect_uri", this.spotifyOAuth2Properties.redirectUri());
    return doAuthApiRequest(formParams);
  }

  public String refreshAccessToken(final String refreshToken) {
    final MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
    formParams.add("refresh_token", refreshToken);
    formParams.add("grant_type", "refresh_token");
    formParams.add("scope", this.spotifyOAuth2Properties.scope());
    return doAuthApiRequest(formParams);
  }

  private String doAuthApiRequest(final MultiValueMap<String, String> formParams) {
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth(this.spotifyOAuth2Properties.getEncodedClientCredentials());
    final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formParams, headers);
    final var exchange =
        this.restTemplate.exchange(
            this.spotifyOAuth2Properties.tokenUri(),
            HttpMethod.POST,
            entity,
            SpotifyAuthResponse.class);
    if (exchange.getStatusCode().isError()) {
      throw new RuntimeException("Cannot exchange the code for an access token");
    }
    final var body = exchange.getBody();
    if (body == null) {
      throw new RuntimeException("Invalid response");
    }
    final var jsonBody = body.toJsonMapper().apply(this.objectMapper);
    final var encryptedTokens = this.encryptionUtils.encrypt(jsonBody);
    return Base64.getEncoder().encodeToString(encryptedTokens.getBytes());
  }

  record SpotifyAuthResponse(
      @JsonProperty("access_token") String accessToken,
      @JsonProperty("expires_in") int expiresIn,
      @JsonProperty("token_type") String tokenType,
      @JsonProperty("refresh_token") String refreshToken) {

    @JsonProperty("invalid_after")
    Instant invalidAfter() {
      return Instant.now(Clock.systemUTC()).plus(expiresIn(), ChronoUnit.SECONDS);
    }

    Function<ObjectMapper, String> toJsonMapper() {
      return mapper -> {
        try {
          return mapper.writeValueAsString(this);
        } catch (final Exception e) {
          throw new RuntimeException(e);
        }
      };
    }
  }
}
