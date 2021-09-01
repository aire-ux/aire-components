package com.aire.ux.theme.servlet.servlet4;

import javax.servlet.http.HttpServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AireServletConfiguration {

  @Bean
  @Scope("singleton")
  public HttpServlet servlet() {
    return new AireThemeResourceServlet();
  }

  @Bean
  @Scope("singleton")
  public ServletRegistrationBean<AireThemeResourceServlet> aireUXScriptManagerServlet() {
    return new ServletRegistrationBean<>(new AireThemeResourceServlet(), "/aire/theme");
  }
}
