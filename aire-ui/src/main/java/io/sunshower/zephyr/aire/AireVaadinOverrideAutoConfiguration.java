package io.sunshower.zephyr.aire;

import com.aire.ux.concurrency.AccessQueue;
import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.spring.RootMappedCondition;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.VaadinConfigurationProperties;
import com.vaadin.flow.spring.VaadinServletConfiguration;
import com.vaadin.flow.spring.VaadinServletContextInitializer;
import com.vaadin.flow.spring.VaadinWebsocketEndpointExporter;
import java.util.HashMap;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@ConditionalOnClass(ServletContextInitializer.class)
@EnableConfigurationProperties(VaadinConfigurationProperties.class)
@Import({VaadinServletConfiguration.class})
public class AireVaadinOverrideAutoConfiguration {

  static final String VAADIN_SERVLET_MAPPING = "/vaadinServlet/*";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private VaadinConfigurationProperties configurationProperties;


  static String makeContextRelative(String url) {
    // / -> context://
    // foo -> context://foo
    // /foo -> context://foo
    if (url.startsWith("/")) {
      url = url.substring(1);
    }
    return "context://" + url;
  }

  @Bean
  public static AccessQueue accessQueue() {
    return new AsynchronousSessionQueue();
  }

  /**
   * Creates a {@link ServletRegistrationBean} instance with Spring aware Vaadin servlet.
   *
   * @return a custom ServletRegistrationBean instance
   */
  @Bean
  public ServletRegistrationBean<SpringServlet> servletRegistrationBean(AccessQueue queue) {
    var mapping = configurationProperties.getUrlMapping();
    val initParameters = new HashMap<String, String>();
    var rootMapping = RootMappedCondition.isRootMapping(mapping);
    if (rootMapping) {
      mapping = VAADIN_SERVLET_MAPPING;
      initParameters.put(
          InitParameters.SERVLET_PARAMETER_PUSH_URL, makeContextRelative(mapping.replace("*", "")));
    }
    val registration =
        new ServletRegistrationBean<SpringServlet>(
            new AireVaadinServlet(queue, context, rootMapping), mapping);
    registration.setInitParameters(initParameters);
    registration.setAsyncSupported(configurationProperties.isAsyncSupported());
    registration.setName(ClassUtils.getShortNameAsProperty(SpringServlet.class));
    return registration;
  }

  /**
   * Creates a {@link ServletContextInitializer} instance.
   *
   * @return a custom ServletContextInitializer instance
   */
  @Bean
  public ServletContextInitializer contextInitializer() {
    return new VaadinServletContextInitializer(context);
  }

  /**
   * Deploys JSR-356 websocket endpoints when Atmosphere is available.
   *
   * @return the server endpoint exporter which does the actual work.
   */
  @Bean
  public ServerEndpointExporter websocketEndpointDeployer() {
    return new VaadinWebsocketEndpointExporter();
  }
}
