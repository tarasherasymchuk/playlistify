package io.playlistify.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserProfileDto(
    @JsonProperty("display_name") String displayName,
    @JsonProperty("external_urls") @JsonIgnore ExternalProfileUrl externalUrl) {

  @JsonProperty("profileUrl")
  @JsonInclude(JsonInclude.Include.NON_ABSENT)
  String getProfileUrl() {
    return externalUrl() == null ? null : externalUrl().profileUrl();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record ExternalProfileUrl(@JsonProperty("spotify") String profileUrl) {}
}
