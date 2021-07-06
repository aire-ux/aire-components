package com.aire.ux.control;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import lombok.val;

@Tag("aire-button")
@JsModule("@aire-ux/aire-button")
@NpmPackage(value = "@aire-ux/aire-button", version = "0.0.16")
public class Button extends Component
    implements HasStyle, HasText, ClickNotifier<Button>, Focusable<Button> {

  /** create an empty button */
  public Button() {}

  /** @param text the text to instantiate this button with */
  public Button(String text) {
    setText(text);
  }

  /**
   * @param text
   * @param clickListener
   */
  public Button(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
    this(text);
    addClickListener(clickListener);
  }

  /**
   * set the text for this button
   *
   * @param text the text to set on this button
   */
  public void setText(String text) {
    removeTextNodes();
    if (text != null) {
      val element = Element.createText(text);
      getElement().appendChild(element);
    }
  }

  /** @param components the components to remove */
  public void removeAll(Component... components) {
    for (val component : components) {
      val child = component.getElement();
      child.removeAttribute("slot");
      getElement().removeChild(child);
    }
  }

  /** @param components the components to add */
  public void addAll(Component... components) {
    for (val component : components) {
      val child = component.getElement();
      child.setAttribute("slot", "");
      getElement().appendChild(child);
    }
  }

  /** remove all the text nodes from this button */
  private void removeTextNodes() {
    for (int i = 0; i < getElement().getChildCount(); i++) {
      val child = getElement().getChild(i);
      if (child.isTextNode()) {
        //        child.removeAttribute("slot");
        getElement().removeChild(i);
      }
    }
  }

  /** click this button--don't fire it in the client */
  public void click() {
    fireEvent(new ClickEvent<>(this, false, 0, 0, 0, 0, 0, 0, false, false, false, false));
  }

  /** click the element in the client */
  public void clickInClient() {
    getElement().callJsFunction("click");
  }
}
