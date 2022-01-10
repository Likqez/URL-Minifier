package dev.dotspace.url.response;

public record GenerationResponse(
    int status,
    String url,
    String image
) {
}
