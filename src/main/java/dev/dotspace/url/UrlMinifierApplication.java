package dev.dotspace.url;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.storage.StorageManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlMinifierApplication {

  public static final String TITLE = "%s | URL-Minifier";

  public static void main(String[] args) {
    ApplicationConfiguration.load(args);
    StorageManager.initialize();
    SpringApplication.run(UrlMinifierApplication.class, args);
  }

}
