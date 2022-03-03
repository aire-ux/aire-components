package com.aire.ux.actions;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultActionMapTest {

  private DefaultActionMap actionMap;

  @BeforeEach
  void setUp() {
    actionMap = new DefaultActionMap();
  }

  @Test
  void ensureRetrievingKeyInMapWorks() {
    val action = new AbstractAction(Key.of("hello.world"), true);
    actionMap.add(action);
    assertEquals(action, actionMap.get(Key.of("hello.world")));
  }

  @Test
  void ensureRetrievingKeyLevelInMapWorks() {
    val action = new AbstractAction(Key.of("hello.world"), true);
    actionMap.add(action);
    assertEquals(List.of(action), actionMap.getKeysIn(Key.of("hello")));
  }
}