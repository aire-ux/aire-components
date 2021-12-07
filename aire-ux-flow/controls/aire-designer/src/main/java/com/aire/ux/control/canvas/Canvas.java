package com.aire.ux.control.canvas;

import com.aire.ux.control.canvas.Constants.Versions;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.NpmPackage;

@Tag("aire-canvas")
@JavaScript("@aire-ux/aire-designer/dist/src/index.js")
@NpmPackage(value = "lit-element", version = Versions.LIT_ELEMENT)
@NpmPackage(value = "@aire-ux/mxgraph", version = Versions.AIRE_UX_MX_GRAPH)
@NpmPackage(value = "@aire-ux/aire-designer", version = Versions.AIRE_UX_DESIGNER_COMPONENT)
public class Canvas extends Component {

  public Canvas() {
    //    configureAttributes();
  }

  //  protected void configureAttributes() {
  //    val element = getElement();
  //    val cfg = getInstance();
  //  }
}
