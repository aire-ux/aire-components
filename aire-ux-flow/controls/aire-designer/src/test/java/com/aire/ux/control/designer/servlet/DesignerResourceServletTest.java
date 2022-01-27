package com.aire.ux.control.designer.servlet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.control.designer.servlet.DesignerResourceServletTest.Cfg;
import com.aire.ux.test.spring.servlet.Client;
import com.aire.ux.test.spring.servlet.EnableAireServlet;
import com.aire.ux.test.spring.servlet.ServletDefinition;
import com.aire.ux.test.spring.servlet.WithServlets;
import javax.inject.Inject;
import javax.script.ScriptException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@EnableAireServlet
@SpringJUnitWebConfig(classes = Cfg.class)
@WithServlets(
    servlets =
        @ServletDefinition(type = DesignerResourceServlet.class, paths = "/aire/designer/**/*"))
@DisabledIfEnvironmentVariable(named = "AIRE_SERVLET_TESTS", matches = "disabled")
class DesignerResourceServletTest {

  @Inject private Client client;

  @Test
  void ensureResourceIsLoadableFromClassPath() {
    val filePath = "ZEPHYR-INF/client/@aire-ux/mxgraph/tsconfig.json";
    assertNotNull(getClass().getClassLoader().getResource(filePath), "file must be loadable here");
  }

  @Test
  void ensureRequestingMxClientWorks() throws ScriptException {
    val result = client.get("/aire/designer/client/@aire-ux/mxgraph/javascript/mxClient.min.js");
    assertNotNull(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"css/common.css", "resources/graph.txt", "resources/editor.txt"})
  void ensureRequestingValuesWorks(String value) {
    val result =
        client.get(
            String.format("/aire/designer/client/@aire-ux/mxgraph/javascript/src/%s", value));
    assertFalse(result.trim().isBlank());
  }

  @ContextConfiguration
  public static class Cfg {}
}
