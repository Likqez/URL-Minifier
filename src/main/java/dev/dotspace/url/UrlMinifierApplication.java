package dev.dotspace.url;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlMinifierApplication {

  public static final String TITLE = "%s | URL-Minifier";

  public static void main(String[] args) {
    SpringApplication.run(UrlMinifierApplication.class, args);
  }

}
