package io.sunshower.zephyr.ui.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.zephyr.AireUITest;
import io.sunshower.zephyr.ui.i18n.InternationalizationBeanPostProcessorTest.Cfg;
import io.sunshower.zephyr.ui.i18n.scenarios.TestView;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@AireUITest
@Routes(scanClassPackage = TestView.class)
@ContextConfiguration(classes = Cfg.class)
class InternationalizationBeanPostProcessorTest {

  @Test
  void ensureResourceBundleIsResolved(@Autowired ResourceBundleResolver resolver) {
    val bundle = resolver.resolve(TestView.class.getName(), Locale.US);
    assertNotNull(bundle);
  }

  @ViewTest
  @Navigate("test-view")
  void ensureLocalizedValuesAreInjected(@Select TestView view) {
    assertNotNull(view);
    assertEquals("Josiah Haswell", view.getName());
  }

  @ViewTest
  @Navigate("test-view")
  void ensureLocalizedValuesAreInjectedIntoHasTextFields(
      @Select("vaadin-vertical-layout vaadin-text-field") TextField textField) {
    assertNotNull(textField);
    assertEquals(textField.getValue(), "waddup");
  }

  @Configuration
  public static class Cfg {

    @Bean
    public static ResourceBundleResolver resourceBundleResolver() {
      return new AireResourceBundleResolver();
    }

    @Bean
    public static InternationalizationBeanPostProcessor internationalizationBeanPostProcessor(
        ApplicationContext context, ResourceBundleResolver resolver) {
      return new InternationalizationBeanPostProcessor(resolver, context);
    }
  }
}
