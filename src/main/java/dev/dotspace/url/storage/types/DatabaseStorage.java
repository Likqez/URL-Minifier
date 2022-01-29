package dev.dotspace.url.storage.types;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.storage.StorageType;
import dev.dotspace.url.util.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.function.Consumer;

public class DatabaseStorage implements StorageType {

  private final Connection connection;
  private final boolean established;

  public DatabaseStorage() {
    var protocol = "jdbc:mariadb://";

    Connection conn = null;
    var success = false;

    try {
      conn = DriverManager.getConnection(
          protocol + ApplicationConfiguration.DATABASE_HOST(),
          ApplicationConfiguration.DATABASE_USER(),
          ApplicationConfiguration.DATABASE_PASSWD()
      );
      success = true;
    } catch (SQLException ignore) {
    } finally {
      this.connection = conn;
      this.established = success;
    }

    createSchemaStructure();
  }

  @Override
  public void established(Consumer<StorageType> success, Runnable onerror) {
    if (this.established) success.accept(this);
    else onerror.run();
  }

  @Override
  public boolean newMinified(String uid, String url, String image) {
    try (var statement = connection.prepareStatement("INSERT INTO url_minifier.minified(uid,url,image) VALUES(?,?,?)")) {

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
  private void createSchemaStructure() {
    try (var statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS url_minifier;")) {
      statement.executeUpdate();
    } catch (SQLException ignore) {
    }

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
            """
    )) {
      statement.executeUpdate();
    } catch (SQLException ignore) {
    }

    try (var statement = connection.prepareStatement(
        """
              create table IF NOT EXISTS url_minifier.analytics
            (
                uid      varchar(8)   not null,
                address varchar(128)  null,
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
