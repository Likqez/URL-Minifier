package dev.dotspace.url;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlMinifierApplication {

  public static final String TITLE = "%s | URL-Minifier";

  /*
  Web Root Path to generate working URLs.
  Protocol needs to be specified.
  E.g.:
    https://url.dotspace.dev/
    https://dotspace.dev/url/
    http://localhost
  */
  public static final String webPath = System.getenv().getOrDefault("minifier-webpath", "http://localhost");

  public static void main(String[] args) {
    SpringApplication.run(UrlMinifierApplication.class, args);
  }

}
