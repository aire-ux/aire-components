package com.aire.features.ui;

import com.aire.features.FeatureDescriptor;
import com.aire.features.FeatureManager;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import io.sunshower.zephyr.ui.controls.Switch;
import lombok.val;

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
    getStyle().set("min-height", "48px");
  }

  private Div createRight() {
    val div = new Div();
    div.getStyle().set("width", "25%");
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

    val firstRow = new Div();
    firstRow.getStyle().set("display", "flex");
    firstRow.getStyle().set("flex-direction", "column");
    firstRow.getStyle().set("width", "auto");
    firstRow.getStyle().set("margin-right", "8px");

    firstRow.add(new Span("Key: " + item.getKey()));
    firstRow.add(new Span("Name: " + item.getKey()));
    div.add(firstRow);

    val secondRow = new Div();
    secondRow.getStyle().set("display", "flex");
    secondRow.getStyle().set("flex-direction", "column");
    secondRow.add(new Span("Description: " + item.getDescription()));
    secondRow.add(new Span("Path" + item.getPath()));

    div.add(secondRow);


//    div.add(keyHolder);

    return div;
  }
}
