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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import lombok.NonNull;
import lombok.val;

/**
 * navigation bar button this type is intended to be used with the NavigationBar type. It can be
 * provided either a content-type for the drawer, a navigation target, or both.
 */
@Tag("aire-navigation-bar-button")
@JsModule("./aire/ui/controls/navigation-bar-button.ts")
@CssImport("./styles/aire/ui/controls/navigation-bar-button.css")
public class NavigationBarButton extends HtmlContainer
    implements ClickNotifier<NavigationBarButton>,
        AfterNavigationObserver,
        Focusable<NavigationBarButton> {

  private final MatchMode matchMode;
  /** immutable state */
  private final List<String> routePatterns;

  private final Class<? extends Component> route;
  private Class<? extends Component> drawerContents;
  private HighlightAction<NavigationBarButton> highlightAction;
  private HighlightCondition<NavigationBarButton> highlightCondition;

  public NavigationBarButton(Class<? extends Component> route, MatchMode mode, String... patterns) {
    this(route, null, List.of(patterns), mode);
  }

  public NavigationBarButton(
      @NonNull Class<? extends Component> route,
      @NonNull Class<? extends Component> drawerContents,
      Component... components) {
    this(route, drawerContents, List.of(routeValue(route)), MatchMode.Prefix, components);
  }

  /**
   * @param route the route to use
   * @param drawerContents the drawer contents
   * @param matchMode the matchmode
   * @param components the components to use
   */
  public NavigationBarButton(
      @NonNull Class<? extends Component> route,
      @Nullable Class<? extends Component> drawerContents,
      MatchMode matchMode,
      Component... components) {
    this(route, drawerContents, List.of(routeValue(route)), matchMode, components);
  }

  /**
   * @param route the route to use
   * @param drawerContents the contents for the drawer(if any)
   * @param patterns the patterns to use
   * @param components the components to add
   */
  public NavigationBarButton(
      @NonNull Class<? extends Component> route,
      @Nullable Class<? extends Component> drawerContents,
      Collection<String> patterns,
      Component... components) {
    this(route, drawerContents, patterns, MatchMode.Prefix, components);
  }

  /**
   * @param route
   * @param components
   */
  public NavigationBarButton(Class<? extends Component> route, Component... components) {
    this(route, (Class<Component>) null, MatchMode.Prefix, components);
  }

  /**
   * @param route the class to use as a route
   * @param drawerContents the contents to place in the drawer (if any)
   * @param toMatch the collection of patterns to match
   * @param matchMode the matchmode to use. Defaults to {@code MatchMode.Prefix}
   * @param components the components to add to this button
   */
  public NavigationBarButton(
      @NonNull Class<? extends Component> route,
      @Nullable Class<? extends Component> drawerContents,
      Collection<String> toMatch,
      MatchMode matchMode,
      Component... components) {
    add(components);
    this.route = route;
    this.matchMode = matchMode;
    this.addClickListener(
        event -> {
          UI.getCurrent().navigate(this.route);
        });
    this.routePatterns = List.copyOf(toMatch);

    /** behaviors */
    configureConditions();
    setDrawer(drawerContents);
  }

  public NavigationBarButton(
      Class<? extends Component> route,
      Collection<String> values,
      MatchMode mode,
      Component... components) {
    this(route, null, values, mode, components);
  }

  private static String routeValue(Class<? extends Component> route) {
    val routeAnnotation = route.getAnnotation(Route.class);
    if (routeAnnotation == null) {
      throw new IllegalStateException("Error: route must be present");
    }
    return routeAnnotation.value();
  }

  public void setDrawer(@Nullable Class<? extends Component> drawerContents) {
    this.drawerContents = drawerContents;
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    highlightAction.highlight(this, highlightCondition.shouldHighlight(this, event));
  }

  protected void configureConditions() {
    highlightAction = HighlightActions.toggleAttribute("focused");
    //    val prefix = Objects.requireNonNull(route.getDeclaredAnnotation(Route.class));
    highlightCondition =
        (navigationBarButton, event) -> {
          val location = event.getLocation();
          val path = location.getPath();
          return routePatterns.stream().anyMatch(matchMode.apply(path));
        };
  }

  public enum MatchMode implements Function<String, Predicate<String>> {
    Prefix {
      @Override
      public Predicate<String> apply(String path) {
        return value -> value.isBlank() ? path.isBlank() : path.startsWith(value);
      }
    },
    Suffix {
      @Override
      public Predicate<String> apply(String path) {
        return value -> value.isBlank() ? path.isBlank() : path.endsWith(value);
      }
    },
    Exact {
      @Override
      public Predicate<String> apply(String path) {
        return value -> value.isBlank() ? path.isBlank() : path.equals(value);
      }
    }
  }
}
