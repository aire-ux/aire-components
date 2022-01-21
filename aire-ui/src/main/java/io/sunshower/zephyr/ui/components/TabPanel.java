package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import io.sunshower.gyre.CompactTrieMap;
import io.sunshower.gyre.RegexStringAnalyzer;
import io.sunshower.gyre.TrieMap;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.val;

@Tag("aire-tab-panel")
@JsModule("./aire/ui/components/tab-panel.ts")
public class TabPanel extends HtmlContainer
    implements RouterLayout, ComponentEventListener<Tabs.SelectedChangeEvent> {

  static final String CLASS_NAME = "tab-panel";
  /** immutable state */
  private final Tabs tabs;

  private final Section contents;
  private final Nav tabContainer;
  private final TrieMap<String, Tab> locations;
  private final Map<Tab, ComponentDescriptor> components;
  /** mutable state */
  private Component current;

  private TabPlacement placement;

  public TabPanel(TabPlacement placement) {
    getClassNames().add(CLASS_NAME);
    tabs = new Tabs();
    decorate(tabs);
    tabContainer = new Nav();
    tabContainer.getElement().setAttribute("slot", "tabs");
    contents = new Section();
    contents.getElement().setAttribute("slot", "content");

    tabContainer.add(tabs);
    add(tabContainer);
    add(contents);
    setTabPlacement(placement);

    components = new HashMap<>();
    locations = new CompactTrieMap<>(new RegexStringAnalyzer("/"));
  }

  public TabPanel() {
    this(TabPlacement.TOP);
  }

  public Tab addTab(String title, Component component) {
    val tab = new Tab(title);
    components.put(tab, new ComponentDescriptor(false, component, null));
    tabs.add(tab);
    return tab;
  }

  public Tab addTab(Component header, Component component) {
    val tab = new Tab(header);
    components.put(tab, new ComponentDescriptor(false, component, null));
    tabs.add(tab);
    return tab;
  }

  public void activate(Tab tab) {
    UI.getCurrent().access(() -> updateTab(components.get(tab)));
  }

  public Tab addTab(String title, Class<? extends Component> componentType) {
    val route = isRoute(componentType);
    if (route) {
      val url = getTargetUrl(componentType);
      val link = new RouterLink(title, componentType);
      val tab = new Tab(link);
      val descriptor = new ComponentDescriptor(true, null, componentType);
      components.put(tab, descriptor);
      tabs.add(tab);
      locations.put(url, tab);
      return tab;
    } else {
      val tab = new Tab(title);
      components.put(tab, new ComponentDescriptor(false, null, componentType));
      tabs.add(tab);
      return tab;
    }
  }

  public void setTabPlacement(TabPlacement placement) {
    val classlist = getClassNames();
    val previousPlacement = this.placement;
    if (previousPlacement != null) {
      classlist.removeIf(t -> t.equals(previousPlacement.name().toLowerCase()));
    }
    this.placement = placement;
    classlist.add(placement.name().toLowerCase());
    updateTabOrientation(placement);
  }

  @Override
  public void onComponentEvent(Tabs.SelectedChangeEvent selectedChangeEvent) {
    val selectedTab = selectedChangeEvent.getSelectedTab();
    val next = components.get(selectedTab);
    if (next != null) {
      UI.getCurrent().access(() -> updateTab(next));
    }
  }

  private void updateTab(@NonNull ComponentDescriptor next) {
    // use default routing mechanism for routes
    if (!next.isRoute) {
      Component nextInstance;
      if (next.instance != null) {
        nextInstance = next.instance;
      } else {
        nextInstance = Instantiator.get(UI.getCurrent()).createComponent(next.componentType);
      }
      setContent(nextInstance);
    }
  }

  private void setContent(Component content) {
    if (current != null) {
      contents.remove(current);
    }
    current = content;
    contents.add(content);
  }

  public void showRouterLayoutContent(HasElement content) {
    UI.getCurrent().access(() -> setContent((Component) content));
  }

  public void removeRouterLayoutContent(HasElement oldContent) {
    UI.getCurrent().access(() -> contents.remove((Component) oldContent));
  }

  private void decorate(Tabs tabs) {
    tabs.addSelectedChangeListener(this);
    tabs.addAttachListener(new ActivateListener());
  }

  private boolean isRoute(Class<? extends Component> componentType) {
    return componentType.isAnnotationPresent(Route.class);
  }

  private void updateTabOrientation(TabPlacement placement) {
    switch (placement) {
      case LEFT:
      case RIGHT:
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        break;
      case TOP:
      case BOTTOM:
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
    }
  }

  private String getTargetUrl(Class<? extends Component> type) {
    val registry = RouteConfiguration.forApplicationScope().getHandledRegistry();
    val result = registry.getTargetUrl(type);
    return result.get();
  }

  private String getCurrentLocation() {
    val ui = UI.getCurrent();
    val internals = ui.getInternals();
    val viewLocation = internals.getActiveViewLocation();
    return viewLocation.getPath();
  }

  public Tab tabForComponent(HasElement content) {
    if (content == null) {
      return null;
    }
    for (val kv : this.components.entrySet()) {
      if (kv.getValue().componentType.equals(content.getClass())) {
        return kv.getKey();
      }
    }
    return null;
  }

  public enum TabPlacement {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
  }

  @AllArgsConstructor
  static class ComponentDescriptor {

    final boolean isRoute;
    final Component instance;
    final Class<? extends Component> componentType;
  }

  final class ActivateListener implements ComponentEventListener<AttachEvent> {

    @Override
    public void onComponentEvent(AttachEvent event) {
      val current = getCurrentLocation();
      val tab = locations.get(current);
      if (tab != null) {
        tabs.setSelectedTab(tab);
      }
    }
  }
}
