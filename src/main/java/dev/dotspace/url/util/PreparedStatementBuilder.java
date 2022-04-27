package dev.dotspace.url.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * Utility class to create PreparedStatements using chained method calls.
 */
public class PreparedStatementBuilder {

  private final PreparedStatement statement;

  private PreparedStatementBuilder(PreparedStatement statement) {
    this.statement = statement;
  }

  public static PreparedStatementBuilder builder(PreparedStatement statement) {
    return new PreparedStatementBuilder(statement);
  }

  public PreparedStatementBuilder setString(int i, String v) throws SQLException {
    this.statement.setString(i, v);
    return this;
  }

  public PreparedStatementBuilder setInt(int i, int v) throws SQLException {
    this.statement.setInt(i, v);
    return this;
  }

  public PreparedStatementBuilder setLong(int i, long v) throws SQLException {
    this.statement.setLong(i, v);
    return this;
  }


  public PreparedStatementBuilder setBool(int i, boolean v) throws SQLException {
    this.statement.setBoolean(i, v);
    return this;
  }

  public PreparedStatementBuilder setTimestamp(int i, Timestamp v) throws SQLException {
    this.statement.setTimestamp(i, v);
    return this;
  }

  public PreparedStatementBuilder setObject(int i, Object v) throws SQLException {
    this.statement.setObject(i, v);
    return this;
  }

  public ResultSet query() throws SQLException {
    return statement.executeQuery();
  }

  public int update() throws SQLException {
    return statement.executeUpdate();
  }

}
