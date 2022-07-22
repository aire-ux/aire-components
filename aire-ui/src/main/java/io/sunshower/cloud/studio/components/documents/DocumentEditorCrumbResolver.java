package io.sunshower.cloud.studio.components.documents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.Session;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.controls.CrumbResolver;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import lombok.val;

public class DocumentEditorCrumbResolver implements CrumbResolver {

  private final Session session;
  private final WorkspaceService workspaceService;

  @Inject
  public DocumentEditorCrumbResolver(
      final Session session, final WorkspaceService workspaceService) {
    this.session = session;
    this.workspaceService = workspaceService;
  }

  @Override
  public Collection<Component> resolve(Breadcrumb crumb, AfterNavigationEvent event) {
    val location = event.getLocation();
    val segments = location.getSegments();
    if (segments.size() >= 4) {
      val wsId = segments.get(1);
      val docId = segments.get(3);
      if (!Identifier.isIdentifier(docId)) {
        return List.of(new Span("New Document"));
      } else {
        val wid = Identifier.valueOf(wsId);
        val did = Identifier.valueOf(docId);
        val manager = workspaceService.createScopedManager(session.getUser());
        val workspace = manager.getWorkspace(wid).get();
        val document = workspace.getDocumentDescriptor(did);
        return List.of(new Span(document.getName() + "." + document.getExtension()));
      }
    }
    return Collections.emptySet();
  }
}
