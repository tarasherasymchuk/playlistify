package io.playlistify.auth;

public record AuthData(boolean authenticated, String accessToken) {}
