package com.aire.features.ui;

import com.aire.features.FeatureDescriptor;
import com.aire.features.FeatureManager;
import com.aire.ux.RouteExtension;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.management.PluginTabView;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

@RouteExtension
@Breadcrumb(name = "Features", icon = "vaadin:plug", host = MainView.class)
@Route(value = "modules/features", layout = PluginTabView.class, registerAtStartup = false)
public class FeatureList extends VerticalLayout
    implements ValueChangeListener<ComponentValueChangeEvent<TextField, String>> {

  private final FeatureManager featureManager;
  private final Grid<FeatureDescriptor> grid;
  private TextField searchField;

  @Autowired
  public FeatureList(FeatureManager featureManager) {
    this.featureManager = featureManager;
    getStyle().set("display", "flex");
    setHeight("100%");

    val menuBar = createMenuBar();
    add(menuBar);

    grid = new Grid<>();
    grid.addComponentColumn(
        (ValueProvider<FeatureDescriptor, FeatureDescriptorListItem>)
            item -> new FeatureDescriptorListItem(item, featureManager));
    grid.setItems(new ListDataProvider<>(featureManager.getDescriptors()));
    add(grid);
  }

  private Component createMenuBar() {
    val result = new MenuBar();
    result.addThemeVariants(
        MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE);

    searchField = new TextField();
    searchField.setPlaceholder("Search");
    searchField.setClearButtonVisible(true);
    searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
    searchField.setValueChangeMode(ValueChangeMode.EAGER);
    searchField.addValueChangeListener(this);
    result.addItem(searchField);

    val addButton = new Button("Add Feature Flag", VaadinIcon.PLUS.create());

    addButton.addClickListener(
        click -> {
          val overlay = Overlays.open(this, AddFeatureFlagOverlay.class);
          overlay.addOverlayClosedEventListener(
              event -> {
                if (!event.isCancelled()) {
                  grid.getDataProvider().refreshAll();
                }
              });
        });

    result.addItem(addButton);

    return result;
  }

  @Override
  public void valueChanged(ComponentValueChangeEvent<TextField, String> event) {}
}
