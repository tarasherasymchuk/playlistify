package io.playlistify.auth;

import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spotify")
public record SpotifyOAuth2Properties(
    String provider,
    String userInfoUri,
    String clientId,
    String clientSecret,
    String redirectUri,
    String authorizationUri,
    String tokenUri,
    String apiUri,
    String scope) {

  public String getEncodedClientCredentials() {
    return Base64.getEncoder().encodeToString((this.clientId + ":" + this.clientSecret).getBytes());
  }
}
