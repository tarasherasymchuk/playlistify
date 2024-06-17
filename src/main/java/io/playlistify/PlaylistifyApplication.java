package io.playlistify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PlaylistifyApplication {

  public static void main(final String[] args) {
    SpringApplication.run(PlaylistifyApplication.class, args);
  }

  @Bean
  RestTemplate restTemplate() {
    final ClientHttpRequestFactory requestFactory =
        new SimpleClientHttpRequestFactory() {
          @Override
          protected void prepareConnection(
              final java.net.HttpURLConnection connection, final String httpMethod)
              throws IOException {
            super.prepareConnection(connection, httpMethod);
            connection.setInstanceFollowRedirects(false);
          }
        };
    return new RestTemplate(requestFactory);
  }

  @Bean
  ObjectMapper objectMapper() {
    final var objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    return objectMapper;
  }
}
