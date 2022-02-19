package dev.dotspace.url.storage.impl;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.response.PageClick;
import dev.dotspace.url.response.exception.StorageException;
import dev.dotspace.url.storage.StorageImplementation;
import dev.dotspace.url.util.PreparedStatementBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DatabaseStorage implements StorageImplementation {

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

    if (this.established) createSchemaStructure();
  }

  @Override
  public void established(Consumer<StorageImplementation> success, Runnable onerror) {
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
      else throw new StorageException();
    }

  }

  public Optional<String> queryUrl(String uid) {
    try (var statement = connection.prepareStatement("SELECT * FROM url_minifier.minified WHERE uid = ?")) {

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

  public boolean deleteMinified(String uid) {
    return false;
  }

  public boolean deleteAllMinified(String url) {
    return false;
  }

  //**************************************/


  @Override
  public void registerClick(String uid, String address, String userAgent, String region) {
    try (var statement = connection.prepareStatement("INSERT INTO url_minifier.analytics(uid, address, userAgent, region) VALUES(?,?,?,?)")) {

      PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .setString(2, address)
          .setString(3, userAgent)
          .setString(4, region)
          .update();

    } catch (Exception ignore) {
    }
  }

  @Override
  public List<PageClick> retrieveAnalytics(String uid) {
    try (var statement = connection.prepareStatement("SELECT * FROM url_minifier.analytics WHERE uid = ?")) {

      var res = PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .query();

      List<PageClick> clickList = new ArrayList<>();
      while (res.next()) {
        clickList.add(new PageClick(
            res.getString("address"),
            res.getString("userAgent"),
            res.getString("region"),
            res.getString("accesstime")
        ));
      }

      return clickList;
    } catch (Exception ignore) {
      return Collections.emptyList();
    }

  }

  /*
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
                userAgent  text         null,
                region   text         not null default 'Unknown',
                accesstime timestamp default CURRENT_TIMESTAMP not null,
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
