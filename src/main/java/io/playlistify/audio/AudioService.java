package io.playlistify.audio;

import io.playlistify.middleware.SpotifyMiddleware;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AudioService {

  private final SpotifyMiddleware spotifyMiddleware;

  public AudioService(final SpotifyMiddleware spotifyMiddleware) {
    this.spotifyMiddleware = spotifyMiddleware;
  }

  public AudioFeatures getAudioFeatures(final String ids) {
    return this.spotifyMiddleware.doGet("/audio-features", AudioFeatures.class, Map.of("ids", ids));
  }
}
