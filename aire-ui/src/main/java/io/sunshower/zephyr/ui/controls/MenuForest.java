package io.sunshower.zephyr.ui.controls;

import com.aire.ux.core.adapters.ComponentHierarchyNodeAdapter;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.shared.Registration;
import io.sunshower.arcus.selectors.css.CssSelectorParser;
import io.sunshower.arcus.selectors.plan.DefaultPlanContext;
import io.sunshower.arcus.selectors.plan.PlanContext;
import io.sunshower.arcus.selectors.test.NodeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.val;

@Tag("aire-menu-forest")
@JsModule("./aire/ui/controls/menu-forest.ts")
@CssImport("./styles/aire/ui/controls/menu-forest.css")
@CssImport(themeFor = "vaadin-button", value = "./styles/aire/ui/controls/vaadin-button.css")
public class MenuForest extends HtmlContainer {

  private static final PlanContext planContext;
  private static final CssSelectorParser selectorParser;
  private static final NodeAdapter<Element> componentNodeAdapter;

  static {
    selectorParser = new CssSelectorParser();
    planContext = DefaultPlanContext.getInstance();
    componentNodeAdapter = new ComponentHierarchyNodeAdapter();
  }

  private final Mode mode;
  private final List<MenuForest> children;
  private final List<Registration> registrations;
  private final Component component;
  private MenuForest currentChild;
  private DomListenerRegistration mouseLeaveRegistration;

  public MenuForest(Mode mode) {
    this(mode, null);
  }

  public MenuForest(Mode mode, Component component) {
    this.mode = mode;
    this.component = component;
    this.children = new ArrayList<>();
    this.registrations = new ArrayList<>();
  }

  public MenuForest() {
    this(Mode.OpenOnHover, null);
  }

  public MenuForest createRoot(Component component) {
    getElement().setAttribute("exportparts", "primary");
    component.getElement().setAttribute("subcontent", true);
    add(component);
    val tree = new MenuForest(mode, component);
    children.add(tree);
    registrations.add(getListenerConfigurer().get().apply(tree));
    return tree;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    this.mouseLeaveRegistration =
        getElement()
            .addEventListener(
                "mouseleave",
                event -> {
                  if (currentChild != null) {
                    closeChild(currentChild);
                  }
                });
    val cfg = getListenerConfigurer();
    getUI()
        .ifPresent(
            ui -> {
              ui.access(
                  () -> {
                    for (val tree : children) {
                      registrations.add(cfg.get().apply(tree));
                    }
                  });
            });
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    getUI()
        .ifPresent(
            ui -> {
              ui.access(
                  () -> {
                    for (val registration : registrations) {
                      registration.remove();
                    }
                  });
            });
    registrations.clear();
    mouseLeaveRegistration.remove();
    if (currentChild != null) {
      closeChild(currentChild);
    }
  }

  public Optional<Component> selectSingle(String selector) {
    return select(selector).stream().findFirst();
  }

  public Set<Component> select(String selector) {
    return selectorParser
        .parse(selector)
        .plan(planContext)
        .evaluate(this.getElement(), componentNodeAdapter)
        .results()
        .stream()
        .flatMap(c -> c.getComponent().stream())
        .collect(Collectors.toUnmodifiableSet());
  }

  private Supplier<Function<MenuForest, Registration>> getListenerConfigurer() {
    if (mode == Mode.OpenOnHover) {
      return OnComponentHoveredConfigurer::new;
    }
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("PMD.NullAssignment")
  private void closeChild(MenuForest child) {
    UI.getCurrent()
        .access(
            () -> {
              child.getElement().removeAttribute("slot");
              if (child.component != null) {
                child.component.getElement().removeAttribute("host");
              }
              remove(child);
              child.getElement().getNode().removeFromTree();
              currentChild = null;
            });
  }

  private void openChild(MenuForest tree) {
    if (currentChild != null) {
      closeChild(currentChild);
    }
    UI.getCurrent()
        .access(
            () -> {
              tree.getElement().setAttribute("slot", "active");
              if (tree.component != null) {
                tree.component.getElement().setAttribute("host", true);
              }
              add(tree);
              currentChild = tree;
            });
  }

  public enum Mode {
    OpenOnClick,
    OpenOnHover
  }

  private class OnComponentHoveredConfigurer implements Function<MenuForest, Registration> {

    @Override
    public Registration apply(final MenuForest forest) {
      val element = forest.component.getElement();
      return element.addEventListener(
          "mouseover",
          (DomEventListener)
              event -> {
                MenuForest.this.openChild(forest);
              });
    }
  }
}
