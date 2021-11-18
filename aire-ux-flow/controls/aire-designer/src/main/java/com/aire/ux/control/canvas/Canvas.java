package com.aire.ux.control.canvas;

import static com.aire.ux.control.designer.servlet.DesignerConfiguration.getInstance;

import com.aire.ux.control.canvas.Constants.Versions;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.val;

@Tag("aire-canvas")
@NpmPackage(value = "lit-element", version = Versions.LIT_ELEMENT)
public class Canvas extends Component {


  public Canvas() {
    configureAttributes();
  }


  protected void configureAttributes() {
    val element = getElement();
    val cfg = getInstance();
    Attributes.BasePath.set(element, cfg.getBasePath());
  }


  public enum Attributes {

    BasePath("base-path");

    final String name;

    Attributes(String name) {
      this.name = name;
    }

    public void set(@NonNull Element element, @NonNull String value) {
      element.setAttribute(name, value);
    }


    @Nullable
    public String get(Element element) {
      return element.getAttribute(name);
    }

  }

}
