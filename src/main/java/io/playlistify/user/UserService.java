package io.playlistify.user;

import org.springframework.stereotype.Component;

import io.playlistify.middleware.SpotifyMiddleware;

@Component
public class UserService {

  private final SpotifyMiddleware spotifyMiddleware;

  public UserService(final SpotifyMiddleware spotifyMiddleware) {
    this.spotifyMiddleware = spotifyMiddleware;
  }

  public UserProfileDto getUserProfile() {
    return this.spotifyMiddleware.doGet("/me", UserProfileDto.class);
  }
}
