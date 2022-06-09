package io.sunshower.cloud.studio.components.documents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLayout;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.Session;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.TabPanel;
import io.sunshower.zephyr.ui.components.TabPanel.Mode;
import javax.inject.Inject;
import lombok.val;

@ParentLayout(MainView.class)
public class AbstractDocumentEditorView extends Panel implements RouterLayout, BeforeEnterObserver {

  private final TabPanel tabs;
  private final Button addButton;
  private final Session session;
  private final WorkspaceService workspaceService;
  private Workspace workspace;
  private WorkspaceManager workspaceManager;
  private Identifier workspaceId;

  @Inject
  public AbstractDocumentEditorView(WorkspaceService workspaceService, Session session) {
    this.session = session;
    this.workspaceService = workspaceService;
    tabs = new TabPanel(TabPanel.Mode.Routes);
//    tabs.setHeight("calc(100% - 40px)");

    tabs.addThemeVariants(
        TabsVariant.LUMO_MINIMAL,
        TabsVariant.LUMO_MINIMAL
    );
    add(tabs);
    addButton = new Button(VaadinIcon.PLUS.create());

    addButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
    addButton.addClickListener(
        click -> {
          val document = new DocumentDescriptor();
          document.setExtension(".tf");
          document.setName("untitled");
          workspace.getOrCreate(document);
          val tab = tabs.addTab("Doc", DocumentEditor.class,
              new RouteParameters(
                  new RouteParam("workspaceId", workspaceId.toString()),
                  new RouteParam("documentId", document.getId().toString())
              ));
          tabs.activate(tab);
        });
    tabs.addControl(addButton);
  }


  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    val workspaceId = Identifier.valueOf(event.getRouteParameters().get("workspaceId").get());
    this.workspaceId = workspaceId;
    this.workspaceManager = workspaceService.createScopedManager(session.getUser());
    this.workspace = workspaceManager.getWorkspace(workspaceId).get();
  }
}
