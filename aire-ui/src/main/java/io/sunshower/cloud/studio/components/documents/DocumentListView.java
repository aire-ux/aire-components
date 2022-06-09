package io.sunshower.cloud.studio.components.documents;

import com.aire.ux.DomAware;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.cloud.studio.components.workspace.WorkspaceListView;
import io.sunshower.model.api.Session;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.controls.FloatingActionButton;
import java.util.Collections;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.val;

@DomAware
@PermitAll
@Route(value = "documents", layout = MainView.class)
@Breadcrumb(name = "Documents", host = WorkspaceListView.class, resolver = DocumentListViewCrumbResolver.class)
public class DocumentListView extends Panel implements HasUrlParameter<String> {

  /***
   * immutable state
   */
  private final Session session;
  private final WorkspaceService service;


  /**
   * mutable state
   */
  private Identifier id;
  private Workspace workspace;
  private WorkspaceManager manager;
  private FloatingActionButton fab;
  private Grid<DocumentDescriptor> documents;
  private WorkspaceDescriptor workspaceDescriptor;

  @Inject
  public DocumentListView(WorkspaceService service, Session session) {
    this.service = service;
    this.session = session;
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    this.id = Identifier.valueOf(parameter);
    this.manager = service.createScopedManager(session.getUser());
    this.workspaceDescriptor = manager.getWorkspaceDescriptor(id).get();
    this.workspace = manager.getWorkspace(workspaceDescriptor).get();
    doLayout();
  }

  private void doLayout() {
    val documents = workspace.getDocuments();
    layoutDocumentList(documents == null ? Collections.emptyList() : documents);
  }


  private void layoutDocumentList(@NonNull List<DocumentDescriptor> docs) {
    documents = new Grid<>();
    fab = new FloatingActionButton(VaadinIcon.PLUS.create());

    fab.addClickListener(click -> {

      val document = new DocumentDescriptor();
      document.setExtension(".tf");
      document.setName("untitled");
      workspace.getOrCreate(document);
      val params = new RouteParameters(
          new RouteParam("workspaceId", id.toString()),
          new RouteParam("documentId", document.getId().toString())
      );
      UI.getCurrent()
          .navigate(DocumentEditor.class, params);
    });
    documents.setItems(new ListDataProvider<>(docs));
    add(documents);
    add(fab);
  }
}
