package com.aire.ux.control.designer.servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.control.designer.servlet.DesignerResourceServletTest.Cfg;
import com.aire.ux.test.spring.servlet.Client;
import com.aire.ux.test.spring.servlet.EnableAireServlet;
import com.aire.ux.test.spring.servlet.ServletDefinition;
import com.aire.ux.test.spring.servlet.WithServlets;
import javax.script.ScriptException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@EnableAireServlet
@SpringJUnitWebConfig(classes = Cfg.class)
@WithServlets(
    servlets = @ServletDefinition(type = DesignerResourceServlet.class, paths = "/aire/designer/**/*.js"))
class DesignerResourceServletTest {

  @Test
  void ensureResourceIsLoadableFromClassPath() {
    val filePath = "ZEPHYR-INF/client/aire-designer/packages/iife/aire-designer.min.js";
    assertNotNull(getClass().getClassLoader().getResource(filePath), "file must be loadable here");
  }

  @Test
  void ensureRequestingMxClientWorks(@Autowired Client client) throws ScriptException {
    val result = client.get("/aire/designer/client/mxgraph/javascript/mxClient.min.js");
    assertNotNull(result);
  }

  @ContextConfiguration
  public static class Cfg {

  }
}
