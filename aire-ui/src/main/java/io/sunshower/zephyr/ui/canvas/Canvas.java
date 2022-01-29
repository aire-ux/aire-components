package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.shared.Registration;
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
@NpmPackage(value = "@aire-ux/aire-condensation", version = "0.1.5")
public class Canvas extends HtmlContainer {

  private Model model;
  private CommandManager commandManager;

  public Model setModel(@NonNull final Model model) {
    val m = this.model;
    this.model = model;
    model.setHost(this);
    this.commandManager = model.getCommandManager();
    return m;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    model.attach(this);
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    super.onDetach(detachEvent);
    model.detach(this);
  }

  public Registration addOnCanvasReadyListener(ComponentEventListener<CanvasReadyEvent> listener) {
    return this.addListener(CanvasReadyEvent.class, listener);
  }

  /**
   * @return the canvas model for this canvas
   */
  public final Model getModel() {
    return model;
  }


}
