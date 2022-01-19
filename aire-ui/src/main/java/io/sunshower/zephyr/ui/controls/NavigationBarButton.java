package io.sunshower.zephyr.ui.controls;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HighlightAction;
import com.vaadin.flow.router.HighlightActions;
import com.vaadin.flow.router.HighlightCondition;
import com.vaadin.flow.router.Route;
import java.util.Objects;
import lombok.val;

@Tag("aire-navigation-bar-button")
@JsModule("./aire/ui/controls/navigation-bar-button.ts")
@CssImport("./styles/aire/ui/controls/navigation-bar-button.css")
public class NavigationBarButton extends HtmlContainer implements
    ClickNotifier<NavigationBarButton>, AfterNavigationObserver, Focusable<NavigationBarButton> {

  private final Class<? extends Component> route;

  private HighlightAction<NavigationBarButton> highlightAction;
  private HighlightCondition<NavigationBarButton> highlightCondition;

  public NavigationBarButton(Class<? extends Component> route) {
    this.route = route;
    configureConditions();
  }

  public NavigationBarButton(Class<? extends Component> route, Component... components) {
    add(components);
    this.route = route;
    this.addClickListener(event -> {
      UI.getCurrent().navigate(this.route);
    });
    configureConditions();
  }


  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    highlightAction.highlight(this, highlightCondition.shouldHighlight(this, event));
  }

  protected void configureConditions() {
    highlightAction = HighlightActions.toggleAttribute("focused");
    val prefix = Objects.requireNonNull(route.getDeclaredAnnotation(Route.class));
    highlightCondition = (navigationBarButton, event) -> {
      val location = event.getLocation();
      val path = location.getPath();
      val routePrefix = prefix.value();
      return routePrefix.isBlank() ? path.isBlank()
          : !routePrefix.isBlank() && path.startsWith(routePrefix);
    };
  }
}
