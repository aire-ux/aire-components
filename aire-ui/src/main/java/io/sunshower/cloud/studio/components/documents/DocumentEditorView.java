package io.sunshower.cloud.studio.components.documents;

import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.Session;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import javax.inject.Inject;
@Breadcrumb(name = "Edit", host = DocumentListView.class, resolver = DocumentEditorViewCrumbResolver.class)
public class DocumentEditorView extends AbstractDocumentEditorView {

  @Inject
  public DocumentEditorView(WorkspaceService workspaceService,
      Session session) {
    super(workspaceService, session);
  }
}
