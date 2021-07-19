package com.aire.ux.test.resolvers;

import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.test.AireTest;
import com.aire.ux.test.Context;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@AireTest
@ExtendWith(MockitoExtension.class)
class UIResolvingElementResolverFactoryTest {

  @ViewTest
  void ensureUIIsInjectable(@Context UI ui) {
    assertNotNull(ui);
  }

  @ViewTest
  void ensureUiCanBeSpied(@Context UI ui) {

  }

}