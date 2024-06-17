package io.playlistify.dashboard;

import io.playlistify.recommendations.RecommendationsService;
import io.playlistify.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexController {

  private final UserService userService;
  private final RecommendationsService recommendationsService;

  public IndexController(
      final UserService userService, final RecommendationsService recommendationsService) {
    this.userService = userService;
    this.recommendationsService = recommendationsService;
  }

  @GetMapping
  ModelAndView dashboard() {
    final ModelAndView mv = new ModelAndView("index");
    final var userProfile = this.userService.getUserProfile();
    final var recommendations = this.recommendationsService.getRecommendations();
    mv.addObject("data", userProfile);
    mv.addObject("recommendations", recommendations);
    return mv;
  }
}
