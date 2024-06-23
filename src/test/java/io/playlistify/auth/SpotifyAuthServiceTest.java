package io.playlistify.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.playlistify.utils.EncryptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

class SpotifyAuthServiceTest {

  private RestTemplate restTemplate;
  private SpotifyOAuth2Properties spotifyOAuth2Properties;
  private EncryptionUtils encryptionUtils;
  private ObjectMapper objectMapper;
  private SpotifyAuthService spotifyAuthService;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    spotifyOAuth2Properties = mock(SpotifyOAuth2Properties.class);
    encryptionUtils = mock(EncryptionUtils.class);
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    spotifyAuthService = new SpotifyAuthService(restTemplate, spotifyOAuth2Properties, encryptionUtils, objectMapper);
  }

  @Test
  void testRequestToken() {
    final String code = "auth_code";
    final String expectedResponse = "encryptedToken";
    final SpotifyAuthService.SpotifyAuthResponse authResponse = new SpotifyAuthService.SpotifyAuthResponse(
            "access_token", 3600, "Bearer", "refresh_token");

    when(spotifyOAuth2Properties.scope()).thenReturn("scope");
    when(spotifyOAuth2Properties.redirectUri()).thenReturn("redirect_uri");
    when(spotifyOAuth2Properties.tokenUri()).thenReturn("token_uri");
    when(spotifyOAuth2Properties.getEncodedClientCredentials()).thenReturn("encoded_credentials");
    when(restTemplate.exchange(eq("token_uri"), eq(HttpMethod.POST), any(HttpEntity.class), eq(SpotifyAuthService.SpotifyAuthResponse.class)))
            .thenReturn(ResponseEntity.ok(authResponse));
    when(encryptionUtils.encrypt(anyString())).thenReturn(expectedResponse);

    final String result = spotifyAuthService.requestToken(code);

    assertEquals(expectedResponse, new String(Base64.getDecoder().decode(result)));
  }

  @Test
  void testRefreshAccessToken() {
    final String refreshToken = "refresh_token";
    final String expectedResponse = "encryptedToken";
    final SpotifyAuthService.SpotifyAuthResponse authResponse = new SpotifyAuthService.SpotifyAuthResponse(
            "new_access_token", 3600, "Bearer", "new_refresh_token");

    when(spotifyOAuth2Properties.scope()).thenReturn("scope");
    when(spotifyOAuth2Properties.tokenUri()).thenReturn("token_uri");
    when(spotifyOAuth2Properties.getEncodedClientCredentials()).thenReturn("encoded_credentials");
    when(restTemplate.exchange(eq("token_uri"), eq(HttpMethod.POST), any(HttpEntity.class), eq(SpotifyAuthService.SpotifyAuthResponse.class)))
            .thenReturn(ResponseEntity.ok(authResponse));
    when(encryptionUtils.encrypt(anyString())).thenReturn(expectedResponse);

    final String result = spotifyAuthService.refreshAccessToken(refreshToken);

    assertEquals(expectedResponse, new String(Base64.getDecoder().decode(result)));
  }
}