package io.aire.ux.demo;

import com.aire.ux.control.designer.servlet.DesignerResourceServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@SpringBootConfiguration
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  public ServletRegistrationBean<DesignerResourceServlet> servletServletRegistrationBean() {
    return new ServletRegistrationBean<>(new DesignerResourceServlet(), "/aire/designer/*");
  }
}
