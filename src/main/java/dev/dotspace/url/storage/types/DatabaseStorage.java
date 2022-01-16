package dev.dotspace.url.storage.types;

import dev.dotspace.url.storage.StorageType;
import dev.dotspace.url.util.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

public class DatabaseStorage implements StorageType {

  private Connection connection;

  /**
   * Initializes Connection to the specified Database.
   * Host, User and Password are read from programm arguments.
   * If no argument is specified it will try to use the following environment variables:
   * <p>
   * minifier_datahost: Host:Port<br>
   * minifier_datausr: Username for login<br>
   * minifier_datapswd: Password for login<br>
   * <p>
   * If arguments are used,<br>
   * the first needs to be the host <br>
   * the second one the user<br>
   * and the third, the password for that user. If the third is not specified, no password will be used.
   *
   * @param args the programm arguments
   * @throws SQLException if connection establishment unsuccessful
   */
  public DatabaseStorage(String... args) throws SQLException {
    if (args != null && args.length != 0) {
      if (args.length == 2) connection = DriverManager.getConnection(args[0], args[1], null);
      if (args.length == 3) connection = DriverManager.getConnection(args[0], args[1], args[2]);
      return;
    }

    var host = "jdbc:mariadb://" + System.getenv().getOrDefault("minifier_datahost", "localhost/");
    var user = System.getenv().getOrDefault("minifier_datausr", "root");
    var pswd = System.getenv().getOrDefault("minifier_datapswd", null);

    connection = DriverManager.getConnection(host, user, pswd);
    connection.setSchema("url_minifier");

    createSchemaStructure();
  }


  @Override
  public boolean newMinified(String uid, String url, String image) {
    try (var statement = connection.prepareStatement("INSERT INTO url_minifier.minified(minified.uid,minified.url, minified.image) VALUES(?,?,?)")) {

      var res = PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .setString(2, url)
          .setString(3, image)
          .update();

      return res == 1; //Success if only one row is manipulated.

    } catch (Exception throwables) {
      if (throwables instanceof SQLIntegrityConstraintViolationException)
        return false; //If Duplicate: don't print message
      throwables.printStackTrace();
    }

    return false;
  }

  public Optional<String> queryUrl(String uid) {
    try (var statement = connection.prepareStatement("SELECT * FROM url_minifier.minified WHERE uid = ?")) {

      var res = PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .query();

      if (res.next())
        return Optional.ofNullable(res.getString("url"));

    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }

    return Optional.empty();
  }

  public boolean deleteMinified(String uid) {
    return false;
  }

  public boolean deleteAllMinified(String url) {
    return false;
  }

  /**
   * Tries to create the needed tables.
   */
  public void createSchemaStructure() {
    try (var statement = connection.prepareStatement(
        """
            create table IF NOT EXISTS url_minifier.minified
            (
                uid   varchar(8) not null
                    primary key,
                url   text       not null,
                image text       not null,
                constraint minified_uid_uindex
                    unique (uid)
            );
            create table IF NOT EXISTS url_minifier.analytics
            (
                uid      varchar(8)   not null,
                address varchar(128) null,
                browser  text         null,
                os       text         null,
                region   text         null,
                constraint uid_analytics_minified_uid_fk
                    foreign key (uid) references url_minifier.minified (uid)
                        on update cascade on delete cascade
            );
            """
    )) {
      statement.executeUpdate();
    } catch (SQLException ignore) {
    }
  }

}
