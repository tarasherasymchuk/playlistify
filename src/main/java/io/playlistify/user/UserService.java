package io.playlistify.user;

import io.playlistify.middleware.SpotifyMiddleware;
import org.springframework.stereotype.Component;

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
