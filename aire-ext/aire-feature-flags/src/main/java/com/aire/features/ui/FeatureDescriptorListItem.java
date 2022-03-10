package com.aire.features.ui;

import com.aire.features.FeatureDescriptor;
import com.aire.features.FeatureManager;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import io.sunshower.zephyr.ui.controls.Switch;
import lombok.val;

@SuppressWarnings("PMD")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class FeatureDescriptorListItem extends HorizontalLayout {

  private final Div leftPanel;
  private final Div rightPanel;
  private final FeatureDescriptor item;
  private final FeatureManager featureManager;

  public FeatureDescriptorListItem(FeatureDescriptor item, FeatureManager featureManager) {
    this.item = item;
    this.featureManager = featureManager;
    leftPanel = createLeft();
    rightPanel = createRight();
    add(leftPanel, rightPanel);
    getStyle().set("min-height", "64px");
  }

  private Div createRight() {
    val div = new Div();
    div.getStyle().set("width", "25%");
    div.getStyle().set("height", "64px");
    div.getStyle().set("flex-direction", "column");
    div.getStyle().set("display", "flex");
    div.getStyle().set("align-items", "flex-end");
    div.getStyle().set("justify-content", "center");

    val enable = new Switch();
    enable.setEnabled(true);
    div.add(enable);
    enable.addSelectionChangeListener(
        changed -> {
          if (changed.isSelected()) {
            featureManager.enable(item.getKey());
          } else {
            featureManager.disable(item.getKey());
          }
        });
    return div;
  }

  private Div createLeft() {
    val div = new Div();
    div.getStyle().set("width", "75%");
    div.getStyle().set("display", "flex");
    div.getStyle().set("flex-direction", "row");

    val col1 = createColumn();
    col1.add(fieldFor("Key:", item.getKey()));
    col1.add(fieldFor("Path:", item.getPath()));
    col1.add(fieldFor("Name:", item.getName()));
    div.add(col1);

    val col2 = createColumn();
    col2.add(fieldFor("Description:", item.getDescription()));
    div.add(col2);

    val col3 = createColumn();
    col3.getStyle().set("flex-direction", "row");
    for (val tag : item.getTags()) {
      val badge = new Span(tag);
      badge.getElement().getThemeList().add("badge");
      badge.getStyle().set("width", "fit-content");
      badge.getStyle().set("height", "fit-content");
      badge.getStyle().set("margin-right", "4px");
      col3.add(badge);
    }
    div.add(col3);
    return div;
  }

  private Component fieldFor(String key, String key1) {
    val div = new Div();
    div.getStyle().set("max-width", "300px");
    div.getStyle().set("white-space", "nowrap");
    div.getStyle().set("overflow", "hidden");
    div.getStyle().set("text-overflow", "ellipsis");

    var span = new Span();
    span.addClassName("primary-10pct");
    span.getStyle().set("font-size", "var(--lumo-font-size-m)");
    span.getStyle().set("color", "var(--lumo-primary-text-color-50pct)");
    span.setText(key);
    span.getStyle().set("margin-right", "12px");

    div.add(span);

    span = new Span();
    span.getStyle().set("font-size", "var(--lumo-font-size-m)");
    span.getStyle().set("color", "var(--lumo-primary-text-color)");
    span.setText(key1);
    div.add(span);
    return div;
  }

  private Div createColumn() {
    val result = new Div();
    result.getStyle().set("display", "flex");
    result.getStyle().set("flex-direction", "column");
    result.getStyle().set("width", "auto");
    result.getStyle().set("margin-right", "8px");
    return result;
  }
}
