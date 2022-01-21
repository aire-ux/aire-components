package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.provider.ListDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;

@Tag("aire-property-panel")
@JsModule("./aire/ui/components/property-panel.ts")
@CssImport("./styles/aire/ui/components/property-panel.css")
public class PropertyPanel extends HtmlContainer {

  @Getter
  private final Header header;
  @Getter
  private final Section content;

  private final Grid<Property> propertyGrid;

  public PropertyPanel() {
    header = new Header();
    header.getElement().setAttribute("slot", "header");
    content = new Section();
    content.getElement().setAttribute("slot", "content");
    add(header);
    add(content);

    propertyGrid = new Grid<>();
    propertyGrid.setMaxHeight("130px");
    propertyGrid.setHeight("130px");
    propertyGrid.addColumn(Property::getKey).setHeader("Key");
    propertyGrid.addColumn(Property::getValue).setHeader("Value");
    propertyGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER);
    setContent(propertyGrid);
  }

  public PropertyPanel(Icon headerIcon, String headerDescription) {
    this();
    headerIcon.setSize("16px");
    setHeaderComponents(headerIcon, new H1(headerDescription));
  }


  public void setHeaderComponents(Component... components) {
    header.removeAll();
    header.add(components);
  }


  public void setContent(Component... components) {
    content.removeAll();
    content.add(components);
  }


  public void setProperties(Collection<Property> properties) {
    if(getChildren().noneMatch(propertyGrid::equals)) {
      content.removeAll();
      content.add(propertyGrid);
    }
    propertyGrid.setItems(new ListDataProvider<>(new ArrayList<>(properties)));
  }


}
