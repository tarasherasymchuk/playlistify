package io.playlistify.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final ModelResponseInterceptor modelResponseInterceptor;

  public WebConfig(final ModelResponseInterceptor modelResponseInterceptor) {
    this.modelResponseInterceptor = modelResponseInterceptor;
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(this.modelResponseInterceptor);
  }
}
