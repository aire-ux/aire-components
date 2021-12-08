package com.aire.ux.control.designer.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.control.designer.servlet.DesignerResourceServletIntegrationTest.Cfg;
import javax.inject.Inject;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@DisabledIfEnvironmentVariable(named = "AIRE_SERVLET_TESTS", matches = "disabled")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Cfg.class)
public class DesignerResourceServletIntegrationTest {

  @Inject private TestRestTemplate template;

  @ParameterizedTest
  @ValueSource(
      strings = {"src/css/common.css", "src/resources/graph.txt", "src/resources/editor.txt"})
  void ensureRequestingValuesWorks(String value) {
    val result =
        template.getForEntity(
            String.format("/aire/designer/client/@aire-ux/mxgraph/javascript/%s", value),
            String.class);
    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
  }

  @Configuration
  public static class Cfg {

    @Bean
    public ServletRegistrationBean<DesignerResourceServlet> servletServletRegistrationBean() {
      return new ServletRegistrationBean<>(new DesignerResourceServlet(), "/aire/designer/*");
    }

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
      return new TomcatServletWebServerFactory();
    }
  }
}
