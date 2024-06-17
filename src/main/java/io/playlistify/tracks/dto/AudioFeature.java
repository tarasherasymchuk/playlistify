package io.playlistify.tracks.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AudioFeature(
    @JsonProperty("id") String id,
    @JsonProperty("analysis_url") String analysisUrl,
    @JsonProperty("track_href") String trackHref,
    /*
      A confidence measure from 0.0 to 1.0 of whether the track is acoustic.
      1.0 represents high confidence the track is acoustic.
      Range: 0 - 1
      Example: 0.00242
    */
    @JsonProperty("acousticness") Double acousticness,
    /*
      Danceability describes how suitable a track is for dancing based on a combination of m
      usical elements including tempo, rhythm stability, beat strength, and overall regularity.
      A value of 0.0 is least danceable and 1.0 is most danceable.
      Example: 0.585
    */
    @JsonProperty("danceability") Double danceability,
    /*
      Energy is a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity.
      Typically, energetic tracks feel fast, loud, and noisy.
      For example, death metal has high energy, while a Bach prelude scores low on the scale.
      Perceptual features contributing to this attribute include dynamic range, perceived loudness,
      timbre, onset rate, and general entropy.
      Example: 0.842
    */
    @JsonProperty("energy") Double energy,
    /*
      Predicts whether a track contains no vocals.
      "Ooh" and "aah" sounds are treated as instrumental in this context.
      Rap or spoken word tracks are clearly "vocal". The closer the instrumentalness value is to 1.0,
      the greater likelihood the track contains no vocal content.
      Values above 0.5 are intended to represent instrumental tracks,
      but confidence is higher as the value approaches 1.0.
      Example: 0.00686
    */
    @JsonProperty("instrumentalness") Double instrumentalness,
    /*
      The key the track is in.
      Integers map to pitches using standard Pitch Class notation. E.g. 0 = C, 1 = C♯/D♭, 2 = D,
      and so on. If no key was detected, the value is -1.
      Range: -1 - 11
      Example: 9
    */
    @JsonProperty("key") Integer key,
    /*
      Detects the presence of an audience in the recording.
      Higher liveness values represent an increased probability that the track was performed live.
      A value above 0.8 provides strong likelihood that the track is live.
      Example: 0.0866
    */
    @JsonProperty("liveness") Double liveness,
    /*
      The overall loudness of a track in decibels (dB).
      Loudness values are averaged across the entire track and are useful
      for comparing relative loudness of tracks.
      Loudness is the quality of a sound
      that is the primary psychological correlate of physical strength (amplitude).
      Values typically range between -60 and 0 db.
      Example: -5.883
    */
    @JsonProperty("loudness") Double loudness,
    /*
      Mode indicates the modality (major or minor) of a track,
      the type of scale from which its melodic content is derived.
      Major is represented by 1 and minor is 0.
      Example: 0
    */
    @JsonProperty("mode") Integer mode,
    /*
      Speechiness detects the presence of spoken words in a track.
      The more exclusively speech-like the recording (e.g. talk show, audio book, poetry),
      the closer to 1.0 the attribute value.
      Values above 0.66 describe tracks that are probably made entirely of spoken words.
      Values between 0.33 and 0.66 describe tracks that may contain both music and speech,
      either in sections or layered, including such cases as rap music.
      Values below 0.33 most likely represent music and other non-speech-like tracks.
      Example: 0.0556
    */
    @JsonProperty("speechiness") Double speechiness,
    /*
      The overall estimated tempo of a track in beats per minute (BPM).
      In musical terminology, tempo is the speed or pace of a given piece and derives directly
      from the average beat duration.
      Example: 118.211
    */
    @JsonProperty("tempo") Double tempo,
    /*
      An estimated time signature.
      The time signature (meter) is a notational convention to specify how many beats are in each bar
      (or measure).
      The time signature ranges from 3 to 7 indicating time signatures of "3/4", to "7/4".
      Range: 3 - 7
      Example: 4
    */
    @JsonProperty("time_signature") Integer timeSignature,
    /*
      A measure from 0.0 to 1.0 describing the musical positiveness conveyed by a track.
      Tracks with high valence sound more positive (e.g. happy, cheerful, euphoric),
      while tracks with low valence sound more negative (e.g. sad, depressed, angry).
      Range: 0 - 1
      Example: 0.428
    */
    @JsonProperty("valence") Double valence) {}
