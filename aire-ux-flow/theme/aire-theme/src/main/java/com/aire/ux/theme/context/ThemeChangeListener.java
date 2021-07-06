package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import com.aire.ux.ThemeResource;
import com.aire.ux.ThemeResource.Type;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.internal.JsonUtils;
import elemental.json.JsonValue;
import io.sunshower.lang.events.Event;
import io.sunshower.lang.events.EventListener;
import io.sunshower.lang.events.EventType;
import java.util.List;
import java.util.logging.Level;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;

@Log
public class ThemeChangeListener implements EventListener<Theme> {


  public static Theme getCurrent() {
    return ThemeContextHolder.getContext().getTheme();
  }


  @Override
  public void onEvent(EventType eventType, Event<Theme> event) {
    setTheme(event.getTarget());
  }

  void setTheme(Theme theme) {
    val ui = UI.getCurrent();
    if (ui == null) {
      log.log(Level.WARNING,
          "Error: UI was null.  Application is probably not initialized yet, "
          + "but the theme should be applied correctly when it is");
      ThemeContextHolder.getContext().setTheme(theme);
      return;
    }
    val page = ui.getPage();
    if(page == null) {
      log.log(Level.WARNING,
          "Error: Page was null.  Application is probably not initialized yet, "
          + "but the theme should be applied correctly when it is");
      ThemeContextHolder.getContext().setTheme(theme);
      return;
    }
    page.executeJs("Aire.ThemeManager.uninstallStyles()");
    val resources = theme.getThemeResources(Type.Stylesheet);
    installStyle(page, theme, resources);
    ThemeContextHolder.getContext().setTheme(theme);
  }

  private void installStyle(Page page, Theme theme, List<ThemeResource> themeResources) {
    val definitions = JsonUtils.createArray(toDefinitions(theme, themeResources));
    page.executeJs("Aire.ThemeManager.installStyles($0)", definitions);
  }

  private JsonValue[] toDefinitions(Theme theme, List<ThemeResource> themeResources) {
    val themeId = theme.getId();
    return themeResources.stream()
        .map(
            resource -> {
              val id = String.format("%s%s", themeId, resource.getName());
              val location = String.format("/aire/theme/current/%s", resource.getName());
              return JsonSerializer.toJson(new StyleDefinition(id, location, "Constructable"));
            })
        .toArray(elemental.json.JsonValue[]::new);
  }

  public enum ThemeChangeEventType implements EventType {
    ThemeChanged,
    ;

    final int id;

    ThemeChangeEventType() {
      this.id = EventType.newId();
    }

    @Override
    public int getId() {
      return id;
    }
  }

  public static class ThemeChangeEvent implements Event<Theme> {


    final Theme theme;

    ThemeChangeEvent(final Theme target) {
      this.theme = target;
    }

    public Theme getTarget() {
      return theme;
    }
  }

  @NoArgsConstructor
  @AllArgsConstructor
  public static class StyleDefinition {

    private String id;
    private String url;
    private String mode = "Constructable";
  }
}
