package io.sunshower.zephyr.ui.editor;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.TabPanel;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.val;

@PermitAll
@Route(value = "editor/test", layout = MainView.class)
@Breadcrumb(name = "Terraform", host = MainView.class)
public class EditorTabPanel extends VerticalLayout {

  private TabPanel tabs;
  private Button addButton;

  @Inject
  public EditorTabPanel() {
    setPadding(false);
    setHeightFull();
    setWidthFull();
    tabs = new TabPanel();
    tabs.setWidthFull();
    tabs.setHeightFull();
    add(tabs);
    configureAddButton();
    tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL, TabsVariant.LUMO_MINIMAL);
  }

  private void configureAddButton() {
    addButton = new Button(VaadinIcon.PLUS.create());
    addButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);

    addButton.addClickListener(click -> {

      val tab = tabs.addTab("Document",
          () -> Instantiator.get(UI.getCurrent()).createComponent(Editor.class));
      tabs.activate(tab);
    });
    tabs.addControl(addButton);
  }
}
