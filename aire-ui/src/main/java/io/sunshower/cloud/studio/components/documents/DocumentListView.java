package io.sunshower.cloud.studio.components.documents;

import com.aire.ux.DomAware;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.cloud.studio.components.workspace.WorkspaceListView;
import io.sunshower.model.api.Session;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;

@DomAware
@PermitAll
@Route(value = "documents", layout = MainView.class)
@Breadcrumb(name = "Documents", host = WorkspaceListView.class, resolver = DocumentListViewCrumbResolver.class)
public class DocumentListView extends Panel implements HasUrlParameter<String> {

  private final WorkspaceService service;
  private final Session session;
  private final Button button;

  @Inject
  public DocumentListView(WorkspaceService service, Session session) {
    this.service = service;
    this.session = session;
    button = new Button();
    add(button);
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    button.setText(parameter);

  }
}
