package com.aire.ux.spring.test.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import com.aire.ux.spring.test.servlet.EnableAireServletTest.Cfg;
import com.aire.ux.test.spring.servlet.Client;
import com.aire.ux.test.spring.servlet.EnableAireServlet;
import com.aire.ux.test.spring.servlet.ServletDefinition;
import com.aire.ux.test.spring.servlet.WithServlets;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.context.WebApplicationContext;

@EnableAireServlet
@WithServlets(servlets = @ServletDefinition(paths = "/test/*", type = TestServlet.class))
@SpringJUnitWebConfig(classes = Cfg.class)
public class EnableAireServletAliasTest {

  @Test
  void ensureTestServletIsInjectable(
      @Autowired TestServlet servlet, @Autowired WebApplicationContext context) {
    assertNotNull(servlet);
  }

  @Test
  @SneakyThrows
  void ensureInvokingClientWorks(@Autowired Client client, @Autowired TestServlet servlet) {
    doAnswer(
            invocation -> {
              MockHttpServletResponse response = invocation.getArgument(1);
              response.getWriter().write("hello!");
              return null;
            })
        .when(servlet.getMockServlet())
        .service(any(), any());
    val result = client.get("/test/hello");
    assertEquals(result, "hello!");
  }
}
