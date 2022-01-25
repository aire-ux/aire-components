package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import lombok.NonNull;
import lombok.val;

@Tag("aire-canvas")
@JsModule("@antv/x6/dist/x6.js")
@CssImport("@antv/x6/dist/x6.css")
@JsModule("./aire/ui/canvas/cell.ts")
@JsModule("./aire/ui/canvas/canvas.ts")
@JsModule("@aire-ux/aire-condensation/dist/index.js")
@CssImport("./styles/aire/ui/canvas/canvas.css")
@NpmPackage(value = "@antv/x6", version = "1.29.6")
@NpmPackage(value = "@aire-ux/aire-condensation", version = "0.1.3")
public class Canvas extends HtmlContainer {


  private Model model;

  public Canvas() {
    this.model = new SharedGraphModel(this);
  }

  public Model setModel(@NonNull final Model model) {
    val m = this.model;
    this.model = model;
    model.setHost(this);
    return m;
  }

}
