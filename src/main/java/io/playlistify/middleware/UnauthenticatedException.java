package io.playlistify.middleware;

public class UnauthenticatedException extends RuntimeException {
  public UnauthenticatedException(final String message) {
    super(message);
  }
}
