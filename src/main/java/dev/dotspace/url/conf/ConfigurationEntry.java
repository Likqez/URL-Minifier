package dev.dotspace.url.conf;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public enum ConfigurationEntry {

  DATABASE_HOST("MINIFIER_DATABASE_HOST", "data:host", "localhost"),
  DATABASE_USER("MINIFIER_DATABASE_USER", "data:usr", "root"),
  DATABASE_PASSWD("MINIFIER_DATABASE_PASSWD", "data:passwd", null),
  DATABASE_PATH("MINIFIER_DATABASE_PATH", "data:path", "local.db"),

  APPLICATION_WEB_PATH("MINIFIER_APPLICATION_WEB_PATH", "web:path", "http://localhost");

  private final String env, arg, def;

  ConfigurationEntry(String env, String arg, String def) {
    this.env = env;
    this.arg = arg;
    this.def = def;
  }



  /*
   * Beispiel Args:
   *
   * -Dserver.port=8888
   * -Ddata.Host=127.0.0.1
   * -Ddata.usr=root
   *
   * java -jar -Ddata.usr=root -Ddata.host=gsso.de URL-Minifier.jar
   * */


}
