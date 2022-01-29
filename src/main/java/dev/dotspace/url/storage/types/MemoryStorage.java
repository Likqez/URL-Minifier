package dev.dotspace.url.storage.types;

import dev.dotspace.url.storage.StorageType;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public class MemoryStorage implements StorageType {

  private final HashMap<String, String> minifiedUrl;
  private final HashMap<String, String> minifiedImage;

  public MemoryStorage() {
    this.minifiedUrl = new HashMap<>();
    this.minifiedImage = new HashMap<>();
  }

  @Override
  public void established(Consumer<StorageType> success, Runnable onerror) {
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
}
