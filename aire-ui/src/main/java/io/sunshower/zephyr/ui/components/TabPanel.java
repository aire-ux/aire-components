package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import io.sunshower.gyre.CompactTrieMap;
import io.sunshower.gyre.RegexStringAnalyzer;
import io.sunshower.gyre.TrieMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

@Tag("aire-tab-panel")
@JsModule("./aire/ui/components/tab-panel.ts")
@CssImport("./styles/aire/ui/components/tab-panel.css")
public class TabPanel extends HtmlContainer
    implements RouterLayout, ComponentEventListener<Tabs.SelectedChangeEvent> {

  static final String CLASS_NAME = "tab-panel";
  /**
   * immutable state
   */
  private final Tabs tabs;

  private Section contents;
  private final Nav tabContainer;
  private final TrieMap<String, Tab> locations;
  @Getter
  private final Map<Tab, ComponentDescriptor> components;
  /**
   * mutable state
   */
  private Component current;

  private TabPlacement placement;

  public enum Mode {
    Routes,
    Contents
  }

  private final Mode mode;

  public TabPanel(TabPlacement placement) {
    this(placement, Mode.Contents);
  }


  public TabPanel(Mode mode) {
    this(TabPlacement.TOP, mode);
  }

  public TabPanel(TabPlacement placement, Mode mode) {
    this.mode = mode;
    getClassNames().add(CLASS_NAME);
    tabs = new Tabs();
    decorate(tabs);
    tabContainer = new Nav();
    tabContainer.getElement().setAttribute("slot", "tabs");
    if(mode == Mode.Contents) {
      contents = new Section();
      contents.getElement().setAttribute("slot", "content");
      add(contents);
    } else {
      addClassName("routes");
    }

    tabContainer.add(tabs);
    add(tabContainer);
    setTabPlacement(placement);

    components = new LinkedHashMap<>();
    locations = new CompactTrieMap<>(new RegexStringAnalyzer("/"));
  }

  public TabPanel() {
    this(TabPlacement.TOP);
  }

  public Tab addTab(String title, Supplier<Component> component) {
    val tab = new Tab(title);
    components.put(tab, new ComponentDescriptor(false, component, null));
    tabs.add(tab);
    return tab;
  }

  public void addControl(Component control) {
    val tab = new Tab(control);
    tab.getElement().getClassList().set("tab-control", true);
    tabs.add(tab);
  }

  public List<Tab> getTabs() {
    return List.copyOf(components.keySet());
  }

  public Tab addTab(Component header, Supplier<Component> component) {
    val tab = new Tab(header);
    components.put(tab, new ComponentDescriptor(false, component, null));
    tabs.add(tab);
    return tab;
  }

  public void activate(HasElement tabComponent) {
    activate(tabForComponent(tabComponent));
  }

  public void activate(Tab tab) {
    tabs.setSelectedTab(tab);
    UI.getCurrent().access(() -> updateTab(components.get(tab), tab));
  }

  public Tab addTab(String title, Class<? extends Component> componentType) {
    return addTab(title, componentType, null);
  }

  public Tab addTab(String title, Class<? extends Component> componentType,
      RouteParameters parameters) {
    val route = isRoute(componentType);
    if (route) {
      val url = getTargetUrl(componentType);
      RouterLink link;
      if (parameters == null) {
        link = new RouterLink(title, componentType);
      } else {
        link = new RouterLink(title, componentType, parameters);
      }
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
      classlist.removeIf(t -> t.equalsIgnoreCase(previousPlacement.name()));
    }
    this.placement = placement;
    classlist.add(placement.name().toLowerCase(Locale.ROOT));
    updateTabOrientation(placement);
  }

  public void addThemeVariants(TabsVariant... variant) {
    tabs.addThemeVariants(variant);
  }

  @Override
  public void onComponentEvent(Tabs.SelectedChangeEvent selectedChangeEvent) {
    val selectedTab = selectedChangeEvent.getSelectedTab();
    val next = components.get(selectedTab);
    if (next != null) {
      tabs.setSelectedTab(selectedTab);
      UI.getCurrent().access(() -> updateTab(next, selectedTab));
    }
  }

  private void updateTab(@NonNull ComponentDescriptor next, Tab selectedTab) {
    // use default routing mechanism for routes
    if(mode == Mode.Routes) {
      return;
    }
    if (!next.isRoute) {
      Component nextInstance;
      if (next.instance != null) {
        nextInstance = next.instance.get();
      } else {
        nextInstance = Instantiator.get(UI.getCurrent()).createComponent(next.componentType);
      }
      setContent(nextInstance);
    }
  }

  private void setContent(Component content) {
    if(mode != Mode.Routes) {
      if (current != null) {
        contents.remove(current);
      }
      current = content;
      contents.add(content);
    }
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
        break;
    }
  }

  private String getTargetUrl(Class<? extends Component> type) {
    val registry = RouteConfiguration.forApplicationScope().getHandledRegistry();
    val result = registry.getTargetUrl(type);
    return result.orElse("");
  }

  private String getCurrentLocation() {
    val ui = UI.getCurrent();
    val internals = ui.getInternals();
    val viewLocation = internals.getActiveViewLocation();
    return viewLocation.getPath();
  }

  public Tab getActiveTab() {
    return tabForComponent(current);
  }

  public Tab tabForComponent(HasElement content) {
    if (content == null) {
      return null;
    }
    for (val kv : this.components.entrySet()) {
      val value = kv.getValue();
      if (isChild(kv.getKey(), content)
          || (!(value == null || value.componentType == null)
              && value.componentType.equals(content.getClass()))) {
        return kv.getKey();
      }
    }
    return null;
  }

  private boolean isChild(Tab t, HasElement content) {
    return t.getChildren().anyMatch(child -> Objects.equals(content, child));
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
    final Supplier<Component> instance;
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
