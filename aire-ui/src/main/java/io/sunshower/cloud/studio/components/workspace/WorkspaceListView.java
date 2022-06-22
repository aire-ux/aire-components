package io.sunshower.cloud.studio.components.workspace;

import com.aire.ux.DomAware;
import com.aire.ux.Element;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.cloud.studio.components.documents.DocumentListView;
import io.sunshower.model.api.Session;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Badge;
import io.sunshower.zephyr.ui.components.Card;
import io.sunshower.zephyr.ui.components.Card.Slot;
import io.sunshower.zephyr.ui.components.DefinitionList;
import io.sunshower.zephyr.ui.components.Drawer;
import io.sunshower.zephyr.ui.components.Overlays;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.controls.FloatingActionButton;
import io.sunshower.zephyr.ui.identicon.Identicon;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.val;

@DomAware
@PermitAll
@Route(value = "workspaces", layout = MainView.class)
@Breadcrumb(name = "Workspaces", host = MainView.class)
public class WorkspaceListView extends Panel {

  private final Session session;
  private final FloatingActionButton fab;
  private final WorkspaceService service;
  private Registration registration;

  private Drawer drawer;
  private TreeGrid<WorkspaceDescriptor> tree;

  @Inject
  public WorkspaceListView(WorkspaceService workspaceService, Session session) {
    setMode(Panel.Mode.Grid);
    Overlays.createHost(this);
    this.session = session;
    this.service = workspaceService;
    this.fab = new FloatingActionButton(VaadinIcon.PLUS.create());
    fab.addClickListener(
        event -> {
          showWorkspaceCreationDialog();
        });
    add(fab);
    relayout();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    if (drawer != null) {
      drawer.remove(tree);
    }
  }

  @Element("aire-drawer.primary-drawer")
  public void drawerElement(Drawer drawer) {
    this.drawer = drawer;
    refreshTree();
  }

  private void refreshTree() {
    if (drawer == null) {
      return;
    }
    if (tree != null) {
      drawer.remove(tree);
    }
    tree = new TreeGrid<>();
    tree.setHeightFull();

    val data = new TreeData<WorkspaceDescriptor>();
    data.addRootItems(service.createScopedManager(session.getUser()).getWorkspaces());
    val provider = new TreeDataProvider<>(data);
    tree.setDataProvider(provider);
    tree.addComponentColumn(
            descriptor -> {
              val column = new HorizontalLayout();
              val icon = Identicon.createFromObject(descriptor.getId());
              column.add(icon);
              val span = new Span(descriptor.getName());
              span.getStyle().set("align-self", "center");
              column.add(span);
              return column;
            })
        .setHeader("Workspaces");
    drawer.add(tree);
  }

  private void showWorkspaceCreationDialog() {
    val overlay = Overlays.open(this, CreateWorkspaceOverlay.class);
    overlay.addOverlayClosedEventListener(
        event -> {
          if (!event.isCancelled()) {
            val notification = new Notification("Successfully created workspace");
            notification.setPosition(Position.TOP_STRETCH);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            notification.setDuration(2000);
            notification.open();
            relayout();
          } else {
            val notification = new Notification("Operation cancelled");
            notification.setPosition(Position.TOP_STRETCH);
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            notification.setDuration(2000);
            notification.open();
          }
        });
  }

  private void relayout() {

    getChildren()
        .forEach(
            c -> {
              if (c instanceof Card) {
                remove(c);
              }
            });

    val workspaces = service.createScopedManager(session.getUser()).getWorkspaces();
    for (val workspace : workspaces) {
      val card = new Card();
      card.setIcon(Identicon.createFromObject(workspace.getId()));
      card.add(Slot.Header, new Text(workspace.getName()));

      val dl =
          new DefinitionList()
              .key("Name")
              .value(new Badge(Badge.Mode.Contrast, workspace.getName()))
              .key("Description")
              .value(new Badge(Badge.Mode.Contrast, workspace.getDescription()));
      card.add(Slot.Content, dl);
      add(card);

      card.addClickListener(
          click -> {
            UI.getCurrent().navigate(DocumentListView.class, workspace.getId().toString());
          });
    }
    refreshTree();
  }
}
