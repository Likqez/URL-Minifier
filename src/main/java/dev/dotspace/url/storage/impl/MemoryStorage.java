package dev.dotspace.url.storage.impl;

import dev.dotspace.url.response.PageClick;
import dev.dotspace.url.storage.StorageImplementation;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MemoryStorage implements StorageImplementation {

  private final HashMap<String, String> minifiedUrl;
  private final HashMap<String, String> minifiedImage;

  public MemoryStorage() {
    this.minifiedUrl = new HashMap<>();
    this.minifiedImage = new HashMap<>();
  }

  @Override
  public void established(Consumer<StorageImplementation> success, Runnable onerror) {
    success.accept(this);
  }

  @Override
  public boolean newMinified(String uid, String url, String image) {
    this.minifiedUrl.put(uid, url);
    this.minifiedImage.put(uid, image);
    return true;
  }

  @Override
  public Optional<String> queryUrl(String uid) {
    return Optional.ofNullable(this.minifiedUrl.getOrDefault(uid, null));
  }

  @Override
  public boolean deleteMinified(String uid) {
    this.minifiedUrl.remove(uid);
    return true;
  }

  @Override
  public boolean deleteAllMinified(String url) {
    return false;
  }

  //**************************************/

  @Override
  public void registerClick(String uid, String address, String userAgent, String region, boolean wasScanned) {
    return;
  }

  @Override
  public List<PageClick> retrieveAnalytics(String uid) {
    return null;
  }
}
