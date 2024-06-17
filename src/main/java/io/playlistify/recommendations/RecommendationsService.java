package io.playlistify.recommendations;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.playlistify.audio.AudioService;
import io.playlistify.middleware.SpotifyMiddleware;
import io.playlistify.tracks.TracksService;
import io.playlistify.tracks.dto.Track;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class RecommendationsService {

  private final SpotifyMiddleware spotifyMiddleware;
  private final TracksService tracksService;
  private final AudioService audioService;

  public RecommendationsService(
      final SpotifyMiddleware spotifyMiddleware,
      final TracksService tracksService,
      final AudioService audioService) {
    this.spotifyMiddleware = spotifyMiddleware;
    this.tracksService = tracksService;
    this.audioService = audioService;
  }

  public RecommendedTracks getRecommendations() {
    final var userTopItems = this.tracksService.getUserTopItems();
    final var audioFeatures = this.audioService.getAudioFeatures(userTopItems.trackIdsString());
    final var moodFeatures = audioFeatures.sameMoodFeatures();
    final var topFiveArtists =
        userTopItems.items().stream()
            .map(Track::artists)
            .flatMap(List::stream)
            .map(Track.Artist::id)
            .collect(groupingBy(Function.identity(), counting()))
            .entrySet()
            .stream()
            .sorted((v1, v2) -> Long.compare(v2.getValue(), v1.getValue()))
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(joining(","));
    moodFeatures.put("seed_artists", topFiveArtists);
    return this.spotifyMiddleware.doGet("/recommendations", RecommendedTracks.class, moodFeatures);
  }

  public record RecommendedTracks(@JsonProperty("tracks") List<Track> tracks) {}
}
