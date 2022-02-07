package dev.dotspace.url.storage.impl;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.response.exception.StorageException;
import dev.dotspace.url.storage.StorageImplementation;
import dev.dotspace.url.util.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.function.Consumer;

public class LocalStorage implements StorageImplementation {

  private final Connection connection;
  private final boolean established;

  public LocalStorage() {
    var success = false;
    Connection conn = null;

    try {
      conn = DriverManager.getConnection("jdbc:sqlite:" + ApplicationConfiguration.DATABASE_PATH());
      success = true;
    } catch (SQLException ignore) {
    } finally {
      this.established = success;
      this.connection = conn;
    }

    if (this.established) createSchemaStructure();
  }

  @Override
  public void established(Consumer<StorageImplementation> success, Runnable onerror) {
    if (this.established) success.accept(this);
    else onerror.run();
  }

  @Override
  public boolean newMinified(String uid, String url, String image) {
    try (var statement = connection.prepareStatement("INSERT INTO minified(uid,url,image) VALUES (?,?,?)")) {

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
      else throw new StorageException();
    }

  }

  @Override
  public Optional<String> queryUrl(String uid) {
    try (var statement = connection.prepareStatement("SELECT * FROM minified WHERE uid = ?")) {

      var res = PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .query();

      if (res.next())
        return Optional.ofNullable(res.getString("url"));

    } catch (SQLException ignore) {
    }

    return Optional.empty();

  }

  @Override
  public boolean deleteMinified(String uid) {
    return false;
  }

  @Override
  public boolean deleteAllMinified(String url) {
    return false;
  }

  //**************************************/


  @Override
  public void registerClick(String uid, String address, String browser, String os, String region) {
    try (var statement = connection.prepareStatement("INSERT INTO analytics(uid, address, browser, os, region) VALUES(?,?,?,?,?)")) {

      PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .setString(2, address)
          .setString(3, browser)
          .setString(4, os)
          .setString(5, region)
          .update();

    } catch (Exception ignore) {
    }
  }

  private void createSchemaStructure() {
    try (var statement = connection.prepareStatement(
        """
            create table IF NOT EXISTS main.minified
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
              create table IF NOT EXISTS main.analytics
            (
                uid      varchar(8)   not null,
                address varchar(128)  null,
                browser  text         null,
                os       text         null,
                region   text         null,
                constraint uid_analytics_minified_uid_fk
                  foreign key (uid) references minified (uid)
                    on update cascade on delete cascade
            );
             """
    )) {
      statement.executeUpdate();
    } catch (SQLException ignore) {
    }

  }
}
