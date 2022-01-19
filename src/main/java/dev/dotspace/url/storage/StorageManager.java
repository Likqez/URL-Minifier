package dev.dotspace.url.storage;

import dev.dotspace.url.storage.types.DatabaseStorage;
import dev.dotspace.url.storage.types.LocalStorage;

import java.sql.SQLException;
import java.util.Optional;

public class StorageManager {

  private static StorageType storageType;

  public static void initialize(String... args) {
    try {
      storageType = new DatabaseStorage(args);
      System.out.println("DatabaseStorage achieved successful connection.");
    } catch (SQLException exception) {
      System.err.println("Couldn't initzialise DatabaseStorage. Trying LocalStorage instead...");
      try {
        storageType = new LocalStorage(args);
        System.out.println("LocalStorage achieved successful connection.");
      } catch (SQLException e) {
        System.err.println("Couldn't initzialise LocalStorage. Exiting...");
        System.exit(1);
      }
    }
  }

  public static Optional<String> queryUrl(String uid) {
    return storageType.queryUrl(uid);
  }

  public static boolean newMinified(String uid, String url, String image) {
    return storageType.newMinified(uid, url, image);
  }

  public static boolean deleteMinified(String uid) {
    return storageType.deleteMinified(uid);
  }

  public static boolean deleteAllMinified(String url) {
    return storageType.deleteMinified(url);
  }

}
