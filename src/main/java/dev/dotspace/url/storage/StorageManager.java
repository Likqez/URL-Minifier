package dev.dotspace.url.storage;

import dev.dotspace.url.storage.impl.DatabaseStorage;
import dev.dotspace.url.storage.impl.LocalStorage;
import dev.dotspace.url.storage.impl.MemoryStorage;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class StorageManager {

  private static final AtomicReference<StorageImplementation> storageImpl = new AtomicReference<>();

  //ExecutorService for asynchronous execution
  private static final ExecutorService executorService = Executors.newFixedThreadPool(2);

  public static void initialize() {
    new DatabaseStorage().established(
        (database) -> {
          storageImpl.set(database);
          System.out.println("DatabaseStorage achieved successful connection.");
        },
        () -> {
          System.err.println("Couldn't initialize DatabaseStorage. Trying LocalStorage instead...");
          new LocalStorage().established(
              local -> {
                storageImpl.set(local);
                System.out.println("LocalStorage achieved successful connection.");
              },
              () -> {
                System.err.println("Couldn't initialize LocalStorage. Using MemoryStorage instead!");
                storageImpl.set(new MemoryStorage());
              });
        });
  }

  public static Optional<String> queryUrl(String uid) {
    return storageImpl.get().queryUrl(uid);
  }

  public static boolean newMinified(String uid, String url, String image, Runnable success) throws RuntimeException {
    boolean b = storageImpl.get().newMinified(uid, url, image);
    if (b) success.run();
    return b;
  }

  public static boolean deleteMinified(String uid) {
    return storageImpl.get().deleteMinified(uid);
  }

  public static boolean deleteAllMinified(String url) {
    return storageImpl.get().deleteMinified(url);
  }

  //**************************************/

  public static void registerClick(String uid, String address, String browser, String os, String region) {
    executorService.execute(() ->
                                storageImpl.get().registerClick(uid, address, browser, os, region));

  }
}
