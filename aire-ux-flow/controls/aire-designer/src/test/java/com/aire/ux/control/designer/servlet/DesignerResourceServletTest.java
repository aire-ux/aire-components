package com.aire.ux.control.designer.servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.control.designer.servlet.DesignerResourceServletTest.Cfg;
import com.aire.ux.test.spring.servlet.Client;
import com.aire.ux.test.spring.servlet.EnableAireServlet;
import com.aire.ux.test.spring.servlet.ServletDefinition;
import com.aire.ux.test.spring.servlet.WithServlets;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@EnableAireServlet
@ContextConfiguration(classes = Cfg.class)
@WithServlets(servlets = @ServletDefinition(type = DesignerResourceServlet.class, paths = "/aire/designer/*"))
class DesignerResourceServletTest {

  @Test
  void ensureResourceIsLoadableFromClassPath() {
    val filePath = "ZEPHYR-INF/client/aire-designer/packages/iife/aire-designer.min.js";
    assertNotNull(getClass().getClassLoader().getResource(filePath), "file must be loadable here");
  }

  @Test
  void ensureClientIsInjectable(@Autowired Client client) {
    

  }

  @ContextConfiguration
  public static class Cfg {

  }
}
