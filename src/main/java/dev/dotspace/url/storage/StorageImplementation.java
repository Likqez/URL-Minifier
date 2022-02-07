package dev.dotspace.url.storage;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @see StorageManager
 */
public interface StorageImplementation {

  void established(Consumer<StorageImplementation> success, Runnable onerror);

  /**
   * Method to insert a new minified link into the storage system.
   * Needs to be retrievable by {@link #queryUrl(String)}
   *
   * @param uid   the identifier
   * @param url   the original url
   * @param image the generated qrimage
   * @return wether the operation was success
   */
  boolean newMinified(String uid, String url, String image);

  /**
   * Filter the storage to find the specified identifier.
   * If uid is found, retrieve the original url and return as Optional.
   * If the identfier is not found, return empty Optional instead.
   *
   * @param uid the identifier
   * @return the Optional
   */
  Optional<String> queryUrl(String uid);

  /**
   * Method to delete one minified link.
   *
   * @param uid the identifier
   * @return wether the operation was success
   */
  boolean deleteMinified(String uid);

  /**
   * Method to delete all minified links which redirect to the given url.
   *
   * @param url the url to check
   * @return wether the operation was success
   */
  boolean deleteAllMinified(String url);
}
