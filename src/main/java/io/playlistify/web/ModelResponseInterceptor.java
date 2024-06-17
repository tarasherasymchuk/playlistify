package io.playlistify.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ModelResponseInterceptor implements HandlerInterceptor {

  private final CookieHandler cookieHandler;

  public ModelResponseInterceptor(final CookieHandler cookieHandler) {
    this.cookieHandler = cookieHandler;
  }

  @Override
  public void postHandle(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Object handler,
      final ModelAndView modelAndView)
      throws IOException {
    final var authData = request.getAttribute("authData");

    //    if (authData == null || modelAndView == null ||
    // !modelAndView.getModel().containsKey("data")) {
    //      this.cookieHandler.clearCookie("tk", request, response);
    //      response.sendRedirect("/login");
    //      return;
    //    }

    if (modelAndView != null) {

      if (modelAndView.getModel().containsKey("data")) {
        modelAndView
            .getModel()
            .compute("data", (k, data) -> Map.of("appData", data, "authData", authData));
      } else {
        if (authData != null) {
          modelAndView.getModel().put("data", Map.of("authData", authData));
        }
      }
    }
  }
}
