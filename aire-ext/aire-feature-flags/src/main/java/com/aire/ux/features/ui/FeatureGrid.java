package com.aire.ux.features.ui;

import com.aire.ux.features.FeatureDescriptor;
import com.aire.ux.features.InMemoryFeatureManager;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.management.PluginTabView;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import lombok.val;

@Breadcrumb(name = "Features", icon = "vaadin:plug", host = MainView.class)
@Route(value = "modules/features", layout = PluginTabView.class)
public class FeatureGrid extends VerticalLayout {


  private final InMemoryFeatureManager featureManager;

  public FeatureGrid() {
    this.featureManager = InMemoryFeatureManager.getInstance();
    getStyle().set("display", "flex");
    setHeight("100%");
    val grid = new Grid<FeatureDescriptor>();
    grid.addColumn((ValueProvider<FeatureDescriptor, String>) FeatureDescriptor::getKey)
        .setHeader("Key")
        .setSortable(true)
        .setResizable(true);

    grid.addColumn((ValueProvider<FeatureDescriptor, String>) FeatureDescriptor::getName)
        .setHeader("Name")
        .setSortable(true)
        .setResizable(true);

    grid.addColumn((ValueProvider<FeatureDescriptor, String>) FeatureDescriptor::getPath)
        .setHeader("Path")
        .setSortable(true)
        .setResizable(true);

    grid.addColumn((ValueProvider<FeatureDescriptor, String>) FeatureDescriptor::getDescription)
        .setHeader("Description")
        .setSortable(true)
        .setResizable(true);
    grid.addColumn((ValueProvider<FeatureDescriptor, Boolean>) FeatureDescriptor::isEnabled)
        .setHeader("Enabled")
        .setSortable(true)
        .setResizable(true);
    grid.setItems(new ListDataProvider<>(featureManager.getDescriptors()));
    add(grid);
  }
}
