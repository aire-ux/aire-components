package io.sunshower.cloud.studio.components.workspace;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.Session;
import io.sunshower.zephyr.ui.components.Overlay;
import io.sunshower.zephyr.ui.components.SplitPanel;
import io.sunshower.zephyr.ui.i18n.Localize;
import io.sunshower.zephyr.ui.i18n.Localized;
import javax.inject.Inject;
import lombok.val;

@Localize
public class CreateWorkspaceOverlay extends Overlay {

  private final Session session;
  private final WorkspaceService workspaceService;

  @Localized("header")
  private String header;

  @Localized("workspace.name.field")
  private String workspaceNameField;

  @Localized("workspace.description.field")
  private String workspaceDescriptionField;

  @Localized("workspace.documentation")
  private String documentation;

  private TextField workspaceName;
  private TextField description;

  @Inject
  public CreateWorkspaceOverlay(WorkspaceService workspaceService, Session session) {
    this.session = session;
    this.workspaceService = workspaceService;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    addHeader();
    doAddContent();
    addFooter();
  }

  private void addHeader() {
    val header = getHeader();
    header.getStyle().set("margin-right", "32px");
    val h1 = new H1(this.header);
    header.add(h1);
    header.add(getCloseButton());
  }

  private void doAddContent() {
    val panel = new SplitPanel();
    panel.getStyle().set("height", "100%");

    val form = new FormLayout();
    workspaceName = new TextField(workspaceNameField);
    description = new TextField(workspaceDescriptionField);
    form.add(workspaceName, description);
    panel.setSecond(form);

    val p = new Paragraph(documentation);
    panel.setFirst(p);

    addContent(panel);
  }

  private void addFooter() {

    val menubar = new MenuBar();
    menubar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_TERTIARY_INLINE);

    val cancel = new Button("Cancel", VaadinIcon.CLOSE.create());
    cancel.addClickListener(event -> this.cancel());

    val install = new Button("Create", VaadinIcon.PLUS_CIRCLE_O.create());
    install.addClickListener(this::onSuccess);
    menubar.addItem(cancel);
    menubar.addItem(install);
    getFooter().add(menubar);
  }

  private void onSuccess(ClickEvent<Button> buttonClickEvent) {
    val name = this.workspaceName.getValue();
    val description = this.description.getValue();
    val descriptor = new WorkspaceDescriptor();
    descriptor.setName(name);
    descriptor.setDescription(description);
    workspaceService.createScopedManager(session.getUser()).createWorkspace(descriptor);
    close();
  }
}
