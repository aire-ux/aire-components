package com.aire.features.ui;

import com.aire.features.FeatureDescriptor;
import com.aire.features.FeatureManager;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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
    add(enable);
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
    val name = new H2(item.getName());
    val description = new H3(item.getDescription());
    add(name);
    add(description);

    return div;
  }
}
