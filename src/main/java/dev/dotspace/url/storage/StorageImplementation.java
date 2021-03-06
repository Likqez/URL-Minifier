package dev.dotspace.url.storage;

import dev.dotspace.url.response.PageClick;

import java.util.List;
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

  /**
   * Method to insert a dataset into analytic storage.
   *
   * @param uid       the identifier
   * @param address   the users address
   * @param userAgent the used agent
   * @param region    the region (from address)
   * @param scanned   the boolean differing between a scanned and normal click
   */
  void registerClick(String uid, String address, String userAgent, String region, boolean scanned);

  /**
   * Retrieves all analytic data from specified identifier
   *
   * @param uid the identifier
   * @return a collection containing all clicks
   */
  List<PageClick> retrieveAnalytics(String uid);
}
