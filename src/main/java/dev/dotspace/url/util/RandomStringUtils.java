package dev.dotspace.url.util;

import java.util.random.RandomGenerator;
import java.util.stream.Collector;

public class RandomStringUtils {

  /**
   * Generates a random String from the specified dataset.
   * Utilizes L32X64MixRandom algorithm.
   * @param size the size of the string
   * @param chars the dataset
   * @return a random String.
   */
  public static String random(int size, String chars) {
    return RandomGenerator.StreamableGenerator.of("L32X64MixRandom")
        .rngs(size)
        .map(randomGenerator -> randomGenerator.nextInt(0, chars.length() - 1))
        .map(chars::charAt)
        .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString));
  }

  /**
   * Uses {@link RandomStringUtils#random(int, String)} )} to generate a random String of 6 letters.
   * It may contain lower and uppercase characters aswell as numbers.
   * @return the generated string
   */
  public static String random() {
    return RandomStringUtils.random(6, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
  }
}
