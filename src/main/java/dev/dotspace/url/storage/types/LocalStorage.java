package dev.dotspace.url.storage.types;

import dev.dotspace.url.storage.StorageType;

import java.util.Optional;

public class LocalStorage implements StorageType {

  @Override
  public boolean newMinified(String uid, String url, String image) {
    return false;
  }

  @Override
  public Optional<String> queryUrl(String uid) {
    return Optional.empty();
  }

  @Override
  public boolean deleteMinified(String uid) {
    return false;
  }

  @Override
  public boolean deleteAllMinified(String url) {
    return false;
  }
}
