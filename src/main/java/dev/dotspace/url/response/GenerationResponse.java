package dev.dotspace.url.response;

public record GenerationResponse(
    int status,
    String msg,
    String url,
    String image
) {

  public GenerationResponse(ResponseStatus status, String url, String image) {
    this(status.asInt(), status.msg(), url, image);
  }

}
