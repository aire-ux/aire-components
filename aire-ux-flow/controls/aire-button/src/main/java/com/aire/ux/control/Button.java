package com.aire.ux.control;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import lombok.val;

@Tag("aire-button")
@JsModule("@aire-ux/aire-button")
@NpmPackage(value = "@aire-ux/aire-button", version = "0.0.14")
public class Button extends Component implements HasStyle, HasText {

  /** create an empty button */
  public Button() {}

  /** @param text the text to instantiate this button with */
  public Button(String text) {
    setText(text);
  }

  public void setText(String text) {
    removeTextNodes();
    if (text != null) {
      val element = Element.createText("text");
      //      element.setAttribute("slot", "");
      getElement().appendChild(element);
    }
  }

  private void removeTextNodes() {
    for (int i = 0; i < getElement().getChildCount(); i++) {
      val child = getElement().getChild(i);
      if (child.isTextNode()) {
        //        child.removeAttribute("slot");
        getElement().removeChild(i);
      }
    }
  }
}
