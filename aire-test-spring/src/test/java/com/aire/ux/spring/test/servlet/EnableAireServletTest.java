package com.aire.ux.spring.test.servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.spring.test.servlet.EnableAireServletTest.Cfg;
import com.aire.ux.test.spring.servlet.EnableAireServlet;
import com.aire.ux.test.spring.servlet.WithServlets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

@EnableAireServlet
@WithServlets(TestServlet.class)
@SpringJUnitWebConfig(classes = Cfg.class)
public class EnableAireServletTest {

  @Test
  void ensureTestServletIsInjectable(@Autowired TestServlet servlet) {
    assertNotNull(servlet);
  }

  @ContextConfiguration
  public static class Cfg {

  }


}
