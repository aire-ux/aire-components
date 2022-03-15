package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import lombok.val;

@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@CssImport(value = "./styles/shared-styles.css", include = "lumo-badge")
public class Badge extends Span {

  public Badge(Mode mode, Icon icon, String text, boolean pill) {
    val el = getElement();
    val tl = el.getThemeList();
    tl.add("badge");
    if (pill) {
      tl.add("pill");
    }
    if (mode != null) {
      tl.add(mode.value);
    }
    if (icon != null) {
      add(icon);
    }
    if (text != null) {
      add(new Text(text));
    }
  }

  public Badge(Mode mode, Icon icon, String text) {
    this(mode, icon, text, false);
  }

  public Badge(Icon icon, String text, boolean pill) {
    this(null, icon, text, pill);
  }

  public Badge(String text, boolean pill) {
    this(null, text, pill);
  }

  public Badge(String text) {
    this(text, false);
  }

  public Badge(Mode mode, Icon component) {
    this(mode, component, "", false);
  }

  public Badge(Mode mode, String text) {
    this(mode, null, text, false);
  }


  public enum Mode {
    Success("success"),
    Error("error"),
    Primary("primary"),
    Contrast("contrast");

    final String value;

    Mode(String value) {
      this.value = value;
    }
  }

}
