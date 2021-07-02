package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import com.aire.ux.ThemeResource;
import com.aire.ux.ThemeResource.Type;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.internal.JsonUtils;
import elemental.json.JsonValue;
import io.sunshower.arcus.reflect.Reflect;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

public class AireThemeManager {

  //  public static ThemeContext getThemeContext() {
  //    return ThemeContextHolder.getContext();
  //  }

  public static <T extends Theme> void setTheme(Class<T> themeType) {
    val ui = UI.getCurrent();
    val page = ui.getPage();
    page.executeJs("Aire.ThemeManager.uninstallStyles()");
    val theme = Reflect.instantiate(themeType);
    val resources = theme.getThemeResources(Type.Stylesheet);
    installStyle(page, theme, resources);
    ThemeContextHolder.getContext().setTheme(theme);
  }

  public static Theme getCurrent() {
    return ThemeContextHolder.getContext().getTheme();
  }

  private static void installStyle(Page page, Theme theme, List<ThemeResource> themeResources) {
    val definitions = JsonUtils.createArray(toDefinitions(theme, themeResources));
    page.executeJs("Aire.ThemeManager.installStyles($0)", definitions);
  }

  private static JsonValue[] toDefinitions(Theme theme, List<ThemeResource> themeResources) {
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

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class StyleDefinition {

    private String id;
    private String url;
    private String mode = "Constructable";
  }
}
