package com.aire.ux.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.aire.ux.control.scenario1.MainView;
import com.aire.ux.test.AireTest;
import com.aire.ux.test.Navigate;
import com.aire.ux.test.Routes;
import com.aire.ux.test.Select;
import com.aire.ux.test.ViewTest;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@AireTest
@Navigate("main")
@ExtendWith(MockitoExtension.class)
@Routes(scanClassPackage = MainView.class)
class ButtonTest {

  @Mock
  ComponentEventListener<ClickEvent<Button>> listener;


  @ViewTest
  void ensureSettingTextWorks(@Select(".setter") Button button) {
    assertEquals("Button:setText", button.getText());
  }

  @ViewTest
  void ensureConstructingButtonWithTextWorks(@Select(".ctor") Button button) {
    assertEquals(button.getText(), "Button::constructorText");
  }

  @Test
  void ensureAddingEventListenerWorks() {
    val button = new Button("test");
    button.addClickListener(listener);
    button.click();
    verify(listener, times(1)).onComponentEvent(any());
  }

}