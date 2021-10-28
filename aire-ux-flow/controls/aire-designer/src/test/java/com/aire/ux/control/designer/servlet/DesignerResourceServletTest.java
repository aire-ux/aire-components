package com.aire.ux.control.designer.servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class DesignerResourceServletTest {

  @Test
  void ensureResourceIsLoadableFromClassPath() {
    val filePath = "ZEPHR-INF/client/aire-designer/packages/iife/aire-designer.min.js";
    assertNotNull(getClass().getClassLoader().getResource(filePath), "file must be loadable here");
  }
}
