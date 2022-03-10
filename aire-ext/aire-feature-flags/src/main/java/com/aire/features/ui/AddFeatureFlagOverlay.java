package com.aire.features.ui;

import com.aire.features.FeatureDescriptor;
import com.aire.features.FeatureManager;
import com.aire.features.RouteDefinitionFeature;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import io.sunshower.zephyr.ui.components.Overlay;
import io.sunshower.zephyr.ui.controls.Switch;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;

public class AddFeatureFlagOverlay extends Overlay {

  public static final String NAME = "name";
  static final String IDENTIFIER_PATTERN =
      "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
  static final Pattern FULLY_QUALIFIED_CLASS_NAME =
      Pattern.compile(IDENTIFIER_PATTERN + "(\\." + IDENTIFIER_PATTERN + ")*");
  private final UserInterface ui;
  private final FeatureManager manager;
  private Switch enabledSwitch;
  private TextField keyInputField;
  private TextField nameInputField;
  private Select<String> pathInputField;
  private TextField tagInputField;
  private TextArea descriptionInput;

  @Autowired
  public AddFeatureFlagOverlay(UserInterface userInterface, FeatureManager manager) {
    this.ui = userInterface;
    this.manager = manager;
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
    keyInputField.getElement().setAttribute(NAME, "key");

    nameInputField = new TextField("Name");
    nameInputField.getElement().setAttribute(NAME, NAME);
    descriptionInput = new TextArea("Description");
    descriptionInput.getElement().setAttribute(NAME, "description");
    pathInputField = new Select<>();
    pathInputField.setLabel("Extension Path");
    pathInputField.setItems(new ListDataProvider<>(getExtensionKeys()));

    tagInputField = new TextField("Tags");
    tagInputField.getElement().setAttribute(NAME, "tags");
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

  void onSuccess(ClickEvent<Button> buttonClickEvent) {
    val value = getValue();
    if (value == null) {
      close();
      return;
    }
    val tagValue = tagInputField.getValue();
    if (tagValue != null) {
      val tags = tagValue.split(",");
      for (val tag : tags) {
        if (!tag.isBlank()) {
          value.addTag(tag);
        }
      }
    }
    manager.registerFeature(value);
    if (enabledSwitch.isSelected()) {
      manager.enable(value.getKey());
    } else {
      manager.disable(value.getKey());
    }
    close();
  }

  private FeatureDescriptor getValue() {

    val inputField = pathInputField.getValue();
    if (inputField != null) {
      if (FULLY_QUALIFIED_CLASS_NAME.matcher(inputField).matches()) {
        val definition =
            ui.getExtensionRegistry().getRouteDefinitions().stream()
                .filter(d -> inputField.equals(d.getComponent().getCanonicalName()))
                .findFirst();
        if (definition.isPresent()) {
          return new RouteDefinitionFeature(
              definition.get(),
              keyInputField.getValue(),
              nameInputField.getValue(),
              descriptionInput.getValue(),
              inputField);
        }
      }
    }
    return new FeatureDescriptor(
        keyInputField.getValue(),
        nameInputField.getValue(),
        pathInputField.getValue(),
        descriptionInput.getValue());
  }
}
