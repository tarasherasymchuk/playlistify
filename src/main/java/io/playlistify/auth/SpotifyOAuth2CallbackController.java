package io.playlistify.auth;

import io.playlistify.web.CookieHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
class SpotifyOAuth2CallbackController {

  private final SpotifyAuthService spotifyAuthService;
  private final CookieHandler cookieHandler;

  public SpotifyOAuth2CallbackController(
      final SpotifyAuthService spotifyAuthService, final CookieHandler cookieHandler) {
    this.spotifyAuthService = spotifyAuthService;
    this.cookieHandler = cookieHandler;
  }

  @GetMapping("/callback")
  String callback(
      final HttpServletResponse response,
      @RequestParam(value = "code", required = false) final String code,
      @RequestParam(value = "state", required = false) final String state,
      @RequestParam(value = "error", required = false) final String error) {
    if (code != null) {
      final var tokens = this.spotifyAuthService.requestToken(code);
      final var cookie = this.cookieHandler.createAuthCookie(tokens);
      response.addHeader("Set-Cookie", cookie.toString());
      return "redirect:/";
    }
    return "redirect:/error";
  }
}
