package io.playlistify.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieHandler {

  public ResponseCookie createAuthCookie(final String value) {
    return ResponseCookie.from("tk").value(value).httpOnly(true).path("/").sameSite("Lax").build();
  }

  public void clearCookie(final Cookie cookie, final HttpServletResponse response) {
    cookie.setMaxAge(0);
    cookie.setAttribute("SameSite", "Lax");
    response.addCookie(cookie);
  }

  public void clearCookie(
      final String cookieName,
      final HttpServletRequest request,
      final HttpServletResponse response) {
    Optional.ofNullable(request.getCookies())
        .flatMap(
            cookies ->
                Arrays.stream(cookies)
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst())
        .ifPresent(
            cookie -> {
              cookie.setMaxAge(0);
              cookie.setAttribute("SameSite", "Lax");
              response.addCookie(cookie);
            });
  }
}
