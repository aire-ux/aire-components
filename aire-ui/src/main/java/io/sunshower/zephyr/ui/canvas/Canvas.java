package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Condensation;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import java.io.IOException;
import lombok.val;

@Tag("aire-canvas")
@JsModule("@antv/x6/dist/x6.js")
@CssImport("@antv/x6/dist/x6.css")
@JsModule("./aire/ui/canvas/canvas.ts")
@JsModule("./aire/ui/canvas/canvas-manager.ts")
@JsModule("@aire-ux/aire-condensation/dist/index.js")
@CssImport("./styles/aire/ui/canvas/canvas.css")
@NpmPackage(value = "@antv/x6", version = "1.29.6")
@NpmPackage(value = "@aire-ux/aire-condensation", version = "0.0.5")
public class Canvas extends HtmlContainer {

  public Canvas() {

    val vertex = new Vertex();
    vertex.setLabel("hello!");
    val button = new Button("Click me");
    button.addClickListener(
        click -> {
          try {
            getElement()
                .callJsFunction("addVertex($0)", Condensation.write("json", Vertex.class, vertex));
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
    //    add(button);
    //
    //    val vertex = new Vertex();
    //    vertex.setLabel("hello!");
    //    button.addClickListener(click -> {
    //      try {
    //        UI.getCurrent().getPage().executeJs("getCanvas($0).setId($1)", "hello",
    //            Condensation.write("json", Vertex.class, vertex));
    //      } catch (IOException e) {
    //        e.printStackTrace();
    //      }
    //    });
  }

  public void add(Element element) {}
}
