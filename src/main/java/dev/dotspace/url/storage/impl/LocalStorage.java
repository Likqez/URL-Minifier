package dev.dotspace.url.storage.impl;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.response.PageClick;
import dev.dotspace.url.response.exception.StorageException;
import dev.dotspace.url.storage.StorageImplementation;
import dev.dotspace.url.util.PreparedStatementBuilder;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  public void registerClick(String uid, String address, String userAgent, String region) {
    try (var statement = connection.prepareStatement("INSERT INTO analytics(uid, address, userAgent, region, accesstime) VALUES(?, ?,?,?,?)")) {

      PreparedStatementBuilder
          .builder(statement)
          .setString(1, uid)
          .setString(2, address)
          .setString(3, userAgent)
          .setString(4, region)
          .setTimestamp(5, Timestamp.from(Instant.now()))
          .update();

    } catch (Exception ignore) {
    }
  }

  @Override
  public List<PageClick> retrieveAnalytics(String uid) {
    try (var statement = connection.prepareStatement("SELECT * FROM analytics WHERE uid = ? ORDER BY accesstime DESC")) {

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
            res.getTimestamp("accesstime")
        ));
      }

      return clickList;
    } catch (Exception ignore) {
      return Collections.emptyList();
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
                userAgent  text         null,
                region   text         not null default 'Unknown',
                accesstime timestamp not null,
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
