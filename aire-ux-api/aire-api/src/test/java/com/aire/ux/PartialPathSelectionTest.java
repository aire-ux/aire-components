package com.aire.ux;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.Test;

class PartialPathSelectionTest {

  @Test
  void ensurePathPrefixWorks() {
    val sel = Selection.path(":hello:world");
    assertEquals(":hello", sel.trunk());
    assertEquals(":world", sel.leaf());
  }

  @Test
  void ensurePathWorksForExample() {
    val sel = Selection.path(":a:b:c:d:e");
    assertEquals(":a:b:c:d", sel.trunk());
    assertEquals(":e", sel.leaf());
  }
}
