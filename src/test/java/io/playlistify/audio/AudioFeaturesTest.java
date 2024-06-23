package io.playlistify.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.playlistify.tracks.dto.AudioFeature;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AudioFeaturesTest {

  private AudioFeatures audioFeatures;

  @BeforeEach
  public void setUp() {
    final List<AudioFeature> audioFeaturesList = List.of(
            new AudioFeature("1", "url1", "track1", 0.5, 0.8, 0.7, 0.1, 1, 0.1, -5.0, 1, 0.05, 120.0, 4, 0.6),
            new AudioFeature("2", "url2", "track2", 0.4, 0.7, 0.6, 0.2, 0, 0.2, -6.0, 0, 0.06, 110.0, 4, 0.5)
    );
    audioFeatures = new AudioFeatures(audioFeaturesList);
  }

  @Test
  void testSameMoodFeatures() {
    final Map<String, Object> moodFeatures = audioFeatures.sameMoodFeatures();
    assertNotNull(moodFeatures);
    assertEquals(0.4, moodFeatures.get("min_acousticness"));
    assertEquals(0.45, moodFeatures.get("target_acousticness"));
    // Add more assertions for other features
  }

  @Test
  void testGetMood() {
    final AudioFeatures.Mood mood = audioFeatures.getMood();
    assertEquals(AudioFeatures.Mood.ENERGETIC, mood);
  }

  @Test
  void testFeatureCalculations() {
    final AudioFeatures.Feature<Double> tempo = audioFeatures.tempo();
    assertEquals(115.0, tempo.avg(), 0.01);

    final AudioFeatures.Feature<Double> energy = audioFeatures.energy();
    assertEquals(0.6, energy.min());
    assertEquals(0.7, energy.max());
    assertEquals(0.65, energy.avg(), 0.01);
  }
}