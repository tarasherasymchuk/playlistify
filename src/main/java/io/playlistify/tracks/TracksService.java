package io.playlistify.tracks;

import static java.util.stream.Collectors.joining;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.playlistify.middleware.SpotifyMiddleware;
import io.playlistify.tracks.dto.Track;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TracksService {

  private final SpotifyMiddleware spotifyMiddleware;

  public TracksService(final SpotifyMiddleware spotifyMiddleware) {
    this.spotifyMiddleware = spotifyMiddleware;
  }

  public TopTracks getUserTopItems() {
    return this.spotifyMiddleware.doGet("/me/top/tracks", TopTracks.class, Map.of("limit", 50));
  }

  public record TopTracks(@JsonProperty("items") List<Track> items) {
    @JsonIgnore
    public String trackIdsString() {
      return items().stream().map(Track::id).collect(joining(","));
    }
  }
}
