package io.sunshower.cloud.studio.home.ui;

import com.vaadin.flow.router.Route;
import io.sunshower.cloud.studio.workflows.WorkflowService;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.i18n.Localize;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.NonNull;

@PermitAll
@Localize
@Breadcrumb(name = "Get Started")
@Route(value = "start", layout = MainView.class)
public class HomeView {

  private final WorkflowService workflowService;

  @Inject
  public HomeView(@NonNull WorkflowService workflowService) {
    this.workflowService = workflowService;
  }
}
