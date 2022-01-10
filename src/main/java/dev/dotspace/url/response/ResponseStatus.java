package dev.dotspace.url.response;

public enum ResponseStatus {
  SUCCESS,
  INVALID_URL,
  UNKNOWN;

  public int asInt() {
    return this.ordinal();
  }
}
