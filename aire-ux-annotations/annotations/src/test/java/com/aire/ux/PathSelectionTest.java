package com.aire.ux;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.val;
import org.junit.jupiter.api.Test;

class PathSelectionTest {

  @Test
  void ensurePathSplitWorksForSinglePath() {
    assertEquals(":", PathSelection.split(":").peek());
  }

  @Test
  void ensurePathSplitWorksForMultipleComponents() {
    val result = new ArrayList<>(PathSelection.split(":hello:world:how:are:you"));
    val expected = List.of(":hello", ":world", ":how", ":are", ":you");
    assertEquals(result, expected);
  }

  @Test
  void ensurePathSplitWorksForMultipleComponents_timed() {
    long average = 0;
    long average2 = 0;
    int count = 10000;
    for (int i = 1; i < count; i++) {
      val path = generatePath(100);

      long sst1 = System.nanoTime();
      val r1 = path.split(":");
      long sst2 = System.nanoTime();
      average2 = average2 + (sst2 - sst1) / i;

      long st1 = System.nanoTime();
      val r2 = PathSelection.split(path);
      long st2 = System.nanoTime();
      average = average + (st2 - st1) / i;
    }
    System.out.println("Average: " + average);
    System.out.println("Average2: " + average2);
    System.out.println(average2 / average);
  }

  private String generatePath(int i) {
    val result = new StringBuilder(i);
    for (int j = 0; j < i; j++) {
      result.append(":").append(generateWord(10));
    }
    return result.toString();
  }

  private String generateWord(int i) {
    val result = new StringBuilder(i);
    val random = new Random();
    val alphabet = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for (int j = 0; j < i; j++) {
      result.append(alphabet.charAt(random.nextInt(alphabet.length())));
    }
    return result.toString();
  }
}
