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
import io.sunshower.zephyr.ui.rmi.ClientResult;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import lombok.NonNull;
import lombok.val;

@Tag("aire-canvas")
@JsModule("@antv/x6/dist/x6.js")
@JsModule("@antv/layout/es/index.js")
@CssImport("@antv/x6/dist/x6.css")
@JsModule("./aire/ui/canvas/cell.ts")
@JsModule("./aire/ui/canvas/canvas.ts")
@JsModule("@aire-ux/aire-condensation/dist/index.js")
@CssImport("./styles/aire/ui/canvas/canvas.css")
@NpmPackage(value = "@antv/x6", version = "1.30.0")
@NpmPackage(value = "@antv/layout", version = "0.1.31")
@NpmPackage(value = "@aire-ux/aire-condensation", version = "0.1.5")
public class Canvas extends HtmlContainer {

  private Model model;
  private CommandManager commandManager;

  public Canvas() {
    setModel(new SharedGraphModel());
  }

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

  public <T> ClientResult<T> invoke(Class<? extends Action<T>> action, Object... arguments) {
    return ClientMethods.withUiSupplier(this).construct(action, arguments).apply(getModel());
  }

  public Registration addOnCanvasReadyListener(ComponentEventListener<CanvasReadyEvent> listener) {
    return addListener(CanvasReadyEvent.class, listener);
  }

  public Registration addOnCanvasClickedEventListener(
      ComponentEventListener<CanvasClickedEvent> listener) {
    return addListener(CanvasClickedEvent.class, listener);
  }

  /** @return the canvas model for this canvas */
  @NonNull
  public final Model getModel() {
    return model;
  }
}
