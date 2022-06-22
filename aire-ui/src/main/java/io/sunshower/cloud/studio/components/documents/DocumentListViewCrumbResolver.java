package io.sunshower.cloud.studio.components.documents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.RouterLink;
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

public class DocumentListViewCrumbResolver implements CrumbResolver {

  private final Session session;
  private final WorkspaceService service;

  @Inject
  public DocumentListViewCrumbResolver(WorkspaceService service, Session session) {
    this.session = session;
    this.service = service;
  }

  @Override
  public Collection<Component> resolve(Breadcrumb crumb, AfterNavigationEvent event) {
    val location = event.getLocation();
    val segments = location.getSegments();
    if (segments.size() > 1) {
      val id = Identifier.valueOf(segments.get(1));
      val manager = service.createScopedManager(session.getUser());
      val descriptor = manager.getWorkspaceDescriptor(id);
      if (descriptor.isPresent()) {
        return List.of(
            new RouterLink("Documents", DocumentListView.class, id.toString()),
            new Span(descriptor.get().getName()));
      }
    }
    return Collections.emptyList();
  }
}
