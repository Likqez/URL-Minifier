package dev.dotspace.url;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.storage.StorageManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UrlMinifierApplication {

  public static final String TITLE = "%s | URL-Minifier";

  /*
  Web Root Path for redirects & URL generation.
  The protocol needs to be specified.
  Also, please make sure the path ends with '/'.
  E.g.:
    https://url.dotspace.dev/
    https://dotspace.dev/url/
    http://localhost/
  */
  public static final String webPath = System.getenv().getOrDefault("minifier_webpath", "http://localhost/");

  public static void main(String[] args) {
    ApplicationConfiguration.load(args);
    StorageManager.initialize();
    SpringApplication.run(UrlMinifierApplication.class, args);
  }

}
