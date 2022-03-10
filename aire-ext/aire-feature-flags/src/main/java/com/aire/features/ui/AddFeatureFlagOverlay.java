package com.aire.features.ui;

import com.aire.features.FeatureDescriptor;
import com.aire.ux.ExtensionDefinition;
import com.aire.ux.UserInterface;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.zephyr.ui.components.Overlay;
import io.sunshower.zephyr.ui.controls.Switch;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

public class AddFeatureFlagOverlay extends Overlay {

  private final UserInterface ui;
  private Switch enabledSwitch;
  private TextField keyInputField;
  private TextField nameInputField;
  private Select<String> pathInputField;
  private TextField tagInputField;
  private TextArea descriptionInput;

  @Getter private FeatureDescriptor value;

  @Autowired
  public AddFeatureFlagOverlay(UserInterface userInterface) {
    this.ui = userInterface;
    addHeader();
    addContent();
    addFooter();
  }

  private void addHeader() {
    val header = getHeader();
    header.add(new H1("Define Feature Flag"));
    header.add(getCloseButton());
  }

  private void addContent() {
    val form = new FormLayout();
    keyInputField = new TextField("Key");

    nameInputField = new TextField("Name");
    descriptionInput = new TextArea("Description");
    pathInputField = new Select<>();
    pathInputField.setLabel("Extension Path");
    pathInputField.setItems(getExtensionKeys());

    tagInputField = new TextField("Tags");
    enabledSwitch = new Switch("Enabled");

    form.add(
        keyInputField,
        nameInputField,
        pathInputField,
        enabledSwitch,
        tagInputField,
        descriptionInput);
    form.setColspan(descriptionInput, 2);
    addContent(form);
  }

  private List<String> getExtensionKeys() {
    return ui.getExtensionRegistry().getExtensions().stream()
        .map(ExtensionDefinition::getPath)
        .collect(Collectors.toList());
  }

  private void addFooter() {
    val menubar = new MenuBar();
    menubar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE);

    val cancel = new Button("Cancel", VaadinIcon.CLOSE.create());
    cancel.addClickListener(event -> this.cancel());

    val install = new Button("Save", VaadinIcon.UPLOAD_ALT.create());
    install.addClickListener(this::onSuccess);
    menubar.addItem(cancel);
    menubar.addItem(install);
    getFooter().add(menubar);
  }

  private void onSuccess(ClickEvent<Button> buttonClickEvent) {
    value =
        new FeatureDescriptor(
            keyInputField.getValue(),
            nameInputField.getValue(),
            pathInputField.getValue(),
            descriptionInput.getValue());

    val tagValue = tagInputField.getValue();
    if (tagValue != null) {
      val tags = tagValue.split(",");
      for (val tag : tags) {
        if (!tag.isBlank()) {
          value.addTag(tag);
        }
      }
    }

    close();
  }
}
