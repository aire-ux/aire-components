package com.aire.ux.test.vaadin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.ViewTest;
import org.junit.jupiter.api.BeforeAll;

@AireTest
class FramesTest_ensureBeforeAllHasCorrectFrame {

  @BeforeAll
  static void ensureFrameMethodIsEmpty() {
    assertTrue(Frames.resolveCurrentFrame().getContext().getTestMethod().isEmpty());
  }

  @ViewTest
  void ensureFrameClassIsNotEmpty() {
    assertFalse(Frames.resolveCurrentFrame().getContext().getTestClass().isEmpty());
  }

}