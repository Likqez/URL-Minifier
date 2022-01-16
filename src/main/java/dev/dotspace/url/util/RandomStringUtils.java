package dev.dotspace.url.util;

import java.util.random.RandomGenerator;
import java.util.stream.Collector;

public class RandomStringUtils {

  public static String random(int size, String chars) {
    return RandomGenerator.StreamableGenerator.of("L32X64MixRandom")
        .rngs(size)
        .map(randomGenerator -> randomGenerator.nextInt(0, chars.length() - 1))
        .map(chars::charAt)
        .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString));
  }

  public static String random() {
    return RandomStringUtils.random(6, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
  }
}
