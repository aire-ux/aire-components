package com.aire.ux.test.spring.servlet;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.sunshower.lang.tuple.Pair;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class DefaultClient implements Client {

  private final MockMvc mvc;
  private final AntPathMatcher matcher;
  private final ConfigurableWebApplicationContext context;

  @Inject
  @SuppressFBWarnings
  public DefaultClient(ConfigurableWebApplicationContext context) {
    this.context = context;
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    this.matcher = new AntPathMatcher();
  }

  @Override
  @SuppressFBWarnings
  public String get(String path) {
    val servlet = locate(path);

    try {
      val request = new MockHttpServletRequest("GET", path);
      request.setParameters(parseParameters(path));
      val response = new MockHttpServletResponse();
      Objects.requireNonNull(context.getServletContext())
          .getRequestDispatcher(path)
          .include(request, response);
      servlet.service(request, response);
      return response.getContentAsString();
    } catch (IOException | ServletException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @SneakyThrows
  private Map<String, String[]> parseParameters(String path) {
    val query = path.substring(path.indexOf("?") + 1, path.length());
    return Pattern.compile("&")
        .splitAsStream(query)
        .map(p -> Arrays.copyOf(p.split("=", 2), 2))
        .collect(groupingBy(s -> s[0], mapping(s -> s[1], toList())))
        .entrySet()
        .stream()
        .map(e -> Pair.of(e.getKey(), e.getValue().toArray(new String[e.getValue().size()])))
        .collect(Collectors.toMap(p -> p.fst, p -> p.snd));
  }

  private Servlet locate(String path) {
    val registrationBeans = context.getBeansOfType(ServletRegistrationBean.class);
    for (val bean : registrationBeans.values()) {
      for (val mapping : bean.getUrlMappings()) {
        if (path.startsWith((String) mapping) || matcher.match((String) mapping, path)) {
          return bean.getServlet();
        }
      }
    }
    throw new NoSuchElementException("No servlet at " + path);
  }

  @Override
  public void post(String path, String body) {}
}
