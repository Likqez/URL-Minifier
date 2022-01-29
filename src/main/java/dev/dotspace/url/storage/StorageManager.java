package dev.dotspace.url.storage;

import dev.dotspace.url.storage.types.DatabaseStorage;
import dev.dotspace.url.storage.types.LocalStorage;
import dev.dotspace.url.storage.types.MemoryStorage;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class StorageManager {

  private static final AtomicReference<StorageType> storageType = new AtomicReference<>();

  public static void initialize() {
    new DatabaseStorage().established(
        (database) -> {
          storageType.set(database);
          System.out.println("DatabaseStorage achieved successful connection.");
        },
        () -> {
          System.err.println("Couldn't initzialise DatabaseStorage. Trying LocalStorage instead...");
          new LocalStorage().established(
              local -> {
                storageType.set(local);
                System.out.println("LocalStorage achieved successful connection.");
              },
              () -> {
                System.err.println("Couldn't initzialise LocalStorage. Using MemoryStorage instead!");
                storageType.set(new MemoryStorage());
              });
        });
  }

  public static Optional<String> queryUrl(String uid) {
    return storageType.get().queryUrl(uid);
  }

  public static boolean newMinified(String uid, String url, String image, Runnable success, Runnable onerror) {
    var b = storageType.get().newMinified(uid, url, image);
    if (b) success.run();
    else onerror.run();
    return b;
  }

  public static boolean newMinified(String uid, String url, String image, Runnable success) {
    var b = storageType.get().newMinified(uid, url, image);
    if (b) success.run();
    return b;
  }

  public static boolean deleteMinified(String uid) {
    return storageType.get().deleteMinified(uid);
  }

  public static boolean deleteAllMinified(String url) {
    return storageType.get().deleteMinified(url);
  }

}
