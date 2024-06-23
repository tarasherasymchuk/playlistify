package io.playlistify.audio;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.playlistify.middleware.SpotifyMiddleware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class AudioServiceTest {

  private SpotifyMiddleware spotifyMiddleware;
  private AudioService audioService;

  @BeforeEach
  public void setUp() {
    spotifyMiddleware = mock(SpotifyMiddleware.class);
    audioService = new AudioService(spotifyMiddleware);
  }

  @Test
  void testGetAudioFeatures() {
    final String ids = "123,456";
    final AudioFeatures audioFeatures = mock(AudioFeatures.class);

    when(spotifyMiddleware.doGet("/audio-features", AudioFeatures.class, Map.of("ids", ids)))
            .thenReturn(audioFeatures);

    final AudioFeatures result = audioService.getAudioFeatures(ids);
    assertNotNull(result);
  }
}