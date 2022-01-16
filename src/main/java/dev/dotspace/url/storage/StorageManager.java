package dev.dotspace.url.storage;

import dev.dotspace.url.storage.types.DatabaseStorage;

import java.sql.SQLException;
import java.util.Optional;

public class StorageManager {

  private static StorageType storageType;

  public static void initialize(String... args) {
    try {
      storageType = new DatabaseStorage(args);
    } catch (SQLException exception) {
      System.getLogger("").log(System.Logger.Level.ERROR, "Couldn't initzialise DatabaseStorage. Exiting..");
      exception.printStackTrace();
      System.exit(1);
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
