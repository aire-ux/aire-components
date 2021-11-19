package com.aire.ux.control.canvas;

import static com.aire.ux.control.designer.servlet.DesignerConfiguration.getInstance;

import com.aire.ux.control.canvas.Constants.Versions;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.dom.Element;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.val;

@Tag("aire-canvas")
@JavaScript("context://aire/designer/client/@aire-ux/mxgraph/javascript/dist/build.js")
@JavaScript("@aire-ux/aire-designer/dist/src/index.js")
@NpmPackage(value = "lit-element", version = Versions.LIT_ELEMENT)
@NpmPackage(value = "@aire-ux/mxgraph", version = Versions.AIRE_UX_MX_GRAPH)
@NpmPackage(value = "@aire-ux/aire-designer", version = Versions.AIRE_UX_DESIGNER_COMPONENT)
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
