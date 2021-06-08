package com.aire.ux.test.spring;

import com.aire.ux.test.vaadin.Frames;
import java.lang.reflect.Method;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class AireSpringVaadinExtension extends SpringExtension {

  public static ApplicationContext getApplicationContext() {
    return SpringExtension.getApplicationContext(Frames.currentContext());
  }

}
