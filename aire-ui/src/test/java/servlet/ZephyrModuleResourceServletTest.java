package servlet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.aire.ux.test.spring.servlet.Client;
import com.aire.ux.test.spring.servlet.EnableAireServlet;
import com.aire.ux.test.spring.servlet.WithServlets;
import io.sunshower.zephyr.AireUITest;
import javax.inject.Inject;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@AireUITest
@EnableAireServlet
@WithServlets(ZephyrModuleResourceServlet.class)
class ZephyrModuleResourceServletTest {

  @Inject
  private ZephyrModuleResourceServlet servlet;

  @Test
  void ensureServletIsInjectable(@Autowired Client client) {
    val result = client.get("/kernel?resource=images/icon.svg");
    assertNotNull(result);
  }


  @Test
  void ensureClientReturnsCorrectValue(@Autowired Client client) {
    val result = client.get("/kernel?resource=images/icon.svg");
    assertTrue(result.contains("svg"));
  }

}