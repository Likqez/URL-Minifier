package dev.dotspace.url.storage;

import dev.dotspace.url.response.PageClick;
import dev.dotspace.url.storage.impl.DatabaseStorage;
import dev.dotspace.url.storage.impl.LocalStorage;
import dev.dotspace.url.storage.impl.MemoryStorage;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * For explanations
 * @see StorageImplementation
 */
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

  public static void newMinified(String uid, String url, String image, Runnable success) throws RuntimeException {
    executorService.execute(() -> {
      boolean b = storageImpl.get().newMinified(uid, url, image);
      if (b) success.run();
    });
  }

  public static boolean deleteMinified(String uid) {
    return storageImpl.get().deleteMinified(uid);
  }

  public static boolean deleteAllMinified(String url) {
    return storageImpl.get().deleteMinified(url);
  }

  //**************************************/

  public static void registerClick(String uid, String address, String userAgent, String region, boolean wasScanned) {
    executorService.execute(() ->
                                storageImpl.get().registerClick(uid, address, userAgent, region, wasScanned));

  }

  public static List<PageClick> retrieveAnalytics(String uid) {
    return storageImpl.get().retrieveAnalytics(uid);
  }
}
