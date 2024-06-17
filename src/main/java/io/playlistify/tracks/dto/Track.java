package io.playlistify.tracks.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Track(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("popularity") Integer popularity,
    @JsonProperty("explicit") Boolean explicit,
    @JsonProperty("external_urls") ExternalUrls externalUrls,
    @JsonProperty("preview_url") String previewUrl,
    @JsonProperty("href") String href,
    @JsonProperty("artists") List<Artist> artists,
    @JsonProperty("album") Album album) {

  public boolean hasPreviewUrl() {
    return previewUrl() != null;
  }

  public record ExternalUrls(@JsonProperty("spotify") String spotify) {}

  public record TrackImage(
      @JsonProperty("url") String url,
      @JsonProperty("height") Integer height,
      @JsonProperty("width") Integer width) {}

  public record Artist(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("href") String href,
      @JsonProperty("external_urls") ExternalUrls externalUrls) {}

  public record Album(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("href") String href,
      @JsonProperty("external_urls") ExternalUrls externalUrls,
      @JsonProperty("images") List<TrackImage> images) {

    public String getCover() {
      return images().getFirst().url();
    }
  }
}
