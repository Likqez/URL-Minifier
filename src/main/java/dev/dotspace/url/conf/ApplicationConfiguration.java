package dev.dotspace.url.conf;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
public class ApplicationConfiguration {

  /* Storage configuration */
  @Getter
  private static String DATABASE_HOST;
  @Getter
  private static String DATABASE_USER;
  @Getter
  private static String DATABASE_PASSWD;
  @Getter
  private static String DATABASE_PATH;

  /* Web configuration */

  /*
  Web Root Path for redirects & URL generation.
  The protocol needs to be specified.
  Also, please make sure the path ends with '/'.
  E.g.:
    https://url.dotspace.dev/
    https://dotspace.dev/url/
    http://localhost/
  */
  @Getter
  private static String APPLICATION_WEB_PATH;


  /**
   * Loads the configuration values from application arguments.
   * Each argument will be tested. If there is no value specified
   * it will be looked up in the systems environmetal varbiables or use a default value.
   */
  public static void load() {
    DATABASE_HOST = getProperty(ConfigurationEntry.DATABASE_HOST);
    DATABASE_USER = getProperty(ConfigurationEntry.DATABASE_USER);
    DATABASE_PASSWD = getProperty(ConfigurationEntry.DATABASE_PASSWD);
    DATABASE_PATH = getProperty(ConfigurationEntry.DATABASE_PATH);

    APPLICATION_WEB_PATH = getProperty(ConfigurationEntry.APPLICATION_WEB_PATH);
  }

  /**
   * Retrieving configuration value from system properties.
   * If no value is found {@link ApplicationConfiguration#envOrDef(ConfigurationEntry)}
   * will be used to determine the default value.
   *
   * @param entry the configurationEntry to retrieve the value
   * @return the configuration value
   * @see ApplicationConfiguration#envOrDef(ConfigurationEntry)
   */
  private static String getProperty(ConfigurationEntry entry) {
    return System.getProperty(entry.arg(), envOrDef(entry));
  }

  /**
   * Retrieving value from system environment.
   * If no value is found, using default values specified in
   * {@link ConfigurationEntry#def()}
   *
   * @param entry the configurationEntry to retrieve the value
   * @return the configuration value
   * @see ApplicationConfiguration#getProperty(ConfigurationEntry)
   */
  private static String envOrDef(ConfigurationEntry entry) {
    return System.getenv().getOrDefault(entry.env(), entry.def());
  }


}
