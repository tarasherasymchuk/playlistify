package io.playlistify.middleware;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(value = {UnauthenticatedException.class})
  String handleUnauthenticatedException() {
    return "index";
  }
}
