package io.playlistify.audio;

import static io.playlistify.audio.AudioFeatures.Feature.calculate;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.playlistify.tracks.dto.AudioFeature;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public record AudioFeatures(@JsonProperty("audio_features") List<AudioFeature> items) {

  private static final int MAJOR_MODE = 1;
  private static final int MINOR_MODE = 0;

  public Map<String, Object> sameMoodFeatures() {
    final var acousticness = acousticness();
    final var tempo = tempo();
    final var energy = energy();
    final var danceability = danceability();
    final var valence = valence();
    final var loudness = loudness();
    final var speechiness = speechiness();
    return new HashMap<>(
        Map.ofEntries(
            Map.entry("min_acousticness", acousticness.min()),
            Map.entry("target_acousticness", acousticness.avg()),
            Map.entry("min_danceability", danceability.min()),
            Map.entry("target_danceability", danceability.avg()),
            Map.entry("min_energy", energy.min()),
            Map.entry("max_acousticness", energy.avg()),
            Map.entry("min_valence", valence.min()),
            Map.entry("target_valence", valence.avg()),
            Map.entry("min_tempo", tempo.min()),
            Map.entry("min_target_tempo", tempo.avg()),
            Map.entry("min_loudness", loudness.min()),
            Map.entry("target_loudness", loudness.avg()),
            Map.entry("min_speechiness", speechiness.min()),
            Map.entry("target_speechiness", speechiness.avg())));
  }

  Feature<Double> tempo() {
    return calculate(AudioFeature::tempo, items());
  }

  Feature<Double> energy() {
    return calculate(AudioFeature::energy, items());
  }

  Feature<Double> danceability() {
    return calculate(AudioFeature::danceability, items());
  }

  Feature<Double> valence() {
    return calculate(AudioFeature::valence, items());
  }

  Feature<Double> acousticness() {
    return calculate(AudioFeature::acousticness, items());
  }

  Feature<Double> loudness() {
    return calculate(AudioFeature::loudness, items());
  }

  Feature<Double> speechiness() {
    return calculate(AudioFeature::speechiness, items());
  }

  Mood getMood() {
    final Mood mood;
    if (energy().avg() > 0.8 && danceability().avg() > 0.7) {
      mood = Mood.HIGH_ENERGY_DANCE; // Prioritize strong energy and dance cues
    } else if (energy().avg() > 0.8) {
      mood = valence().avg() > 0.6 ? Mood.UPBEAT : Mood.INTENSE;
    } else if (danceability().avg() > 0.7 && energy().avg() > 0.5) {
      mood =
          valence().avg() > 0.6
              ? Mood.HAPPY
              : Mood.ENERGETIC; // Dance with some energy leans energetic/happy
    } else if (valence().avg() > 0.6) {
      mood = Mood.CALM; // Positive with lower energy/dance
    } else {
      mood = Mood.SAD; // Lower energy/dance and negative valence
    }
    return mood;
  }

  Feature<Integer> mode() {
    final var modeOccurrences =
        items().stream().map(AudioFeature::mode).collect(groupingBy(identity(), counting()));
    final var majorModeOccurrences = modeOccurrences.getOrDefault(MAJOR_MODE, 0L);
    final var minorModeOccurrences = modeOccurrences.getOrDefault(MINOR_MODE, 0L);
    final var comparableValue = majorModeOccurrences.compareTo(minorModeOccurrences);

    final var valence = valence();
    return new Feature<>(
        comparableValue > 0 ? MAJOR_MODE : MINOR_MODE,
        comparableValue < 0 ? MINOR_MODE : MAJOR_MODE,
        comparableValue == 0 && valence.avg() >= 0.6 ? MAJOR_MODE : MINOR_MODE);
  }

  enum Mood {
    UPBEAT,
    INTENSE,
    HAPPY,
    ENERGETIC,
    CALM,
    SAD,
    HIGH_ENERGY_DANCE
  }

  public record Feature<T>(T min, T max, double avg) {
    static Feature<Double> calculate(
        final ToDoubleFunction<AudioFeature> supplier, final List<AudioFeature> items) {
      final double avg = items.stream().mapToDouble(supplier).average().orElse(0);
      final double min = BigDecimal.valueOf(avg).setScale(1, RoundingMode.FLOOR).doubleValue();
      final double max = BigDecimal.valueOf(avg).setScale(1, RoundingMode.CEILING).doubleValue();
      return new Feature<>(min, max, avg);
    }
  }
}
