package io.sunshower.cloud.studio.components.documents;

import static io.sunshower.cloud.studio.Document.read;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import io.sunshower.cloud.studio.Document;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.Session;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.editor.Editor;
import io.sunshower.zephyr.ui.editor.actions.GetEditorContentsAction;
import io.sunshower.zephyr.ui.editor.actions.SetEditorContentsAction;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.SneakyThrows;
import lombok.val;

@PermitAll
@Route(value = "documents/:workspaceId/edit/:documentId", layout = AbstractDocumentEditorView.class)
@Breadcrumb(
    name = "Edit",
    host = DocumentEditorView.class,
    resolver = DocumentEditorCrumbResolver.class)
public class DocumentEditor extends VerticalLayout implements BeforeEnterObserver {

  static final ComponentEventListener<ClickEvent<MenuItem>> NULL_LISTENER = e -> {};
  private final Editor editor;
  private final Session session;
  private final WorkspaceService service;
  //  private final Editor editor;
  private Identifier documentId;
  private Identifier workspaceId;
  private MenuBar menubar;
  private Workspace workspace;
  private DocumentDescriptor document;
  private Document actualDocument;

  @Inject
  public DocumentEditor(Session session, WorkspaceService service) {

    this.session = session;
    this.service = service;
    menubar = new MenuBar();
    menubar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_SMALL);
    createMenubar();
    add(menubar);
    setPadding(false);
    setSpacing(false);
    getStyle().set("flex", "1 1");
    getStyle().set("min-height", "0");

    editor = new Editor();
    editor.getElement().setAttribute("frapper", "wapper");
    add(editor);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    try {
      val docContents = read(actualDocument);
      if (!(docContents == null || docContents.isEmpty())) {
        editor.invoke(SetEditorContentsAction.class, docContents.toString());
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void createMenubar() {
    val fileMenuBar = createIconItem(menubar, VaadinIcon.FILE_O, "File");
    val submenu = fileMenuBar.getSubMenu();
    createIconItem(
        submenu,
        VaadinIcon.FILE_ADD,
        "Save",
        e -> {
          editor
              .invoke(GetEditorContentsAction.class)
              .then(
                  value -> {
                    val svalue = value.asString();
                    actualDocument.write(
                        new ByteArrayInputStream(svalue.getBytes(StandardCharsets.UTF_8)));
                    service.createScopedManager(session.getUser()).flush();
                  });
        });

    val editMenuBar = createIconItem(menubar, VaadinIcon.EDIT, "Edit");
    val codeMenuBar = createIconItem(menubar, VaadinIcon.CODE, "Code");
  }

  private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label) {
    return createIconItem(menu, iconName, label, null, false, NULL_LISTENER);
  }

  private MenuItem createIconItem(
      HasMenuItems menu,
      VaadinIcon iconName,
      String label,
      ComponentEventListener<ClickEvent<MenuItem>> listener) {
    return createIconItem(menu, iconName, label, null, false, listener);
  }

  private MenuItem createIconItem(
      HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel) {
    return createIconItem(menu, iconName, label, ariaLabel, false, NULL_LISTENER);
  }

  private MenuItem createIconItem(
      HasMenuItems menu,
      VaadinIcon iconName,
      String label,
      String ariaLabel,
      ComponentEventListener<ClickEvent<MenuItem>> listener) {
    return createIconItem(menu, iconName, label, ariaLabel, false, listener);
  }

  private MenuItem createIconItem(
      HasMenuItems menu,
      VaadinIcon iconName,
      String label,
      String ariaLabel,
      boolean isChild,
      ComponentEventListener<ClickEvent<MenuItem>> listener) {
    val icon = new Icon(iconName);

    if (isChild) {
      icon.getStyle().set("width", "var(--lumo-icon-size-s)");
      icon.getStyle().set("height", "var(--lumo-icon-size-s)");
      icon.getStyle().set("marginRight", "var(--lumo-space-s)");
    }

    MenuItem item = menu.addItem(icon, listener);

    if (ariaLabel != null) {
      item.getElement().setAttribute("aria-label", ariaLabel);
    }

    if (label != null) {
      item.add(new Text(label));
    }
    return item;
  }

  @Override
  @SneakyThrows
  public void beforeEnter(BeforeEnterEvent event) {
    val params = event.getRouteParameters();
    workspaceId = Identifier.valueOf(params.get("workspaceId").get());
    workspace = service.createScopedManager(session.getUser()).getWorkspace(workspaceId).get();
    //    documentId = Identifier.valueOf(params.get("documentId").get());
    val isNew = params.get("documentId").map(t -> t.equals("new")).orElse(true);
    if (isNew) {
      document = new DocumentDescriptor();
      document.setName("untitled");
      document.setExtension(".tf");
    } else {
      documentId = params.get("documentId").map(Identifier::valueOf).get();
      document = workspace.getDocumentDescriptor(documentId);
    }
    actualDocument = workspace.getOrCreate(document);
  }
}
