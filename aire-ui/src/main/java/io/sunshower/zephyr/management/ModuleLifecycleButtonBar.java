package io.sunshower.zephyr.management;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.shared.Registration;
import io.zephyr.cli.Zephyr;
import io.zephyr.kernel.Module;
import java.util.List;
import lombok.Getter;
import lombok.val;

@Getter
public class ModuleLifecycleButtonBar extends MenuBar {

  private final Button stopButton;
  private final Button startButton;
  private final Button restartButton;
  private final Registration startRegistration;
  private final Registration stopRegistration;
  private final Registration restartRegistration;

  public ModuleLifecycleButtonBar(Grid<Module> grid, Zephyr zephyr, Module module) {
    addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
    startButton = createButton(VaadinIcon.PLAY_CIRCLE, "Start Module", ButtonVariant.LUMO_SUCCESS);
    stopButton = createButton(VaadinIcon.STOP, "Stop Module", ButtonVariant.LUMO_ERROR);
    restartButton = createButton(VaadinIcon.REFRESH, "Restart Module", ButtonVariant.LUMO_PRIMARY);
    startRegistration = configureStartAction(grid, zephyr, module);
    stopRegistration = configureStopRegistration(grid, zephyr, module);
    restartRegistration = configureRestartRegistration(grid, zephyr, module);
    updateStatus(module);
  }

  public Registration addModuleLifecycleChangedLister(
      ComponentEventListener<ModuleLifecycleChangedEvent> listener) {
    return addListener(ModuleLifecycleChangedEvent.class, listener);
  }

  private void updateStatus(Module module) {
    val lifecycle = module.getLifecycle();
    switch (lifecycle.getState()) {
      case Active:
        getStartButton().setEnabled(false);
        break;
      case Installed, Uninstalled, Resolved:
        getStartButton().setEnabled(true);
        getStopButton().setEnabled(false);
        getRestartButton().setEnabled(false);
    }
  }

  private Registration configureRestartRegistration(
      Grid<Module> grid, Zephyr zephyr, Module module) {
    return restartButton.addClickListener(
        click -> {
          grid.select(module);
          zephyr.stop(List.of(module.getCoordinate().toCanonicalForm()));
          zephyr.start(module.getCoordinate().toCanonicalForm());
          fireEvent(new ModuleLifecycleChangedEvent(this, false));
          grid.getDataProvider().refreshAll();
        });
  }

  private Registration configureStopRegistration(Grid<Module> grid, Zephyr zephyr, Module module) {
    return stopButton.addClickListener(
        click -> {
          grid.select(module);
          zephyr.stop(List.of(module.getCoordinate().toCanonicalForm()));
          fireEvent(new ModuleLifecycleChangedEvent(this, false));
          stopButton.setEnabled(false);
          startButton.setEnabled(true);
          restartButton.setEnabled(false);
          grid.getDataProvider().refreshAll();
        });
  }

  private Registration configureStartAction(Grid<Module> grid, Zephyr zephyr, Module module) {
    return startButton.addClickListener(
        click -> {
          grid.select(module);
          zephyr.start(module.getCoordinate().toCanonicalForm());
          fireEvent(new ModuleLifecycleChangedEvent(this, false));
          stopButton.setEnabled(true);
          startButton.setEnabled(false);
          restartButton.setEnabled(false);
          grid.getDataProvider().refreshAll();
        });
  }

  private Button createButton(VaadinIcon icon, String description, ButtonVariant variant) {
    val result = new Button(icon.create());
    result.addThemeVariants(
        ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY, variant);
    result.getElement().setAttribute("aria-label", description);
    addItem(result);
    return result;
  }

  public static class ModuleLifecycleChangedEvent extends ComponentEvent<ModuleLifecycleButtonBar> {

    /**
     * Creates a new event using the given source and indicator whether the event originated from
     * the client side or the server side.
     *
     * @param source the source component
     * @param fromClient <code>true</code> if the event originated from the client
     */
    public ModuleLifecycleChangedEvent(ModuleLifecycleButtonBar source, boolean fromClient) {
      super(source, fromClient);
    }
  }
}
