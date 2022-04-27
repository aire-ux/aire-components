package io.sunshower.zephyr.configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

public class SecurityRequestCache extends HttpSessionRequestCache {
  @Override
  public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
    if (!SecurityUtils.isFrameworkInternalRequest(request)) {
      super.saveRequest(request, response);
    }
  }
}
