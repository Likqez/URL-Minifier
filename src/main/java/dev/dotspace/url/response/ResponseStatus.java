package dev.dotspace.url.response;

public enum ResponseStatus {
  SUCCESS("ok"),
  INVALID_URL("invalid URL specified"),
  INVALID_JSON("invalid request body"),
  INVALID_ARGUMENTS("invalid arguments specified"),
  UNKNOWN("error!");

  private final String msg;
  ResponseStatus(String s) {
    this.msg = s;
  }

  public String msg() {
    return msg;
  }

  public int asInt() {
    return this.ordinal();
  }
}
