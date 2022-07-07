package io.sunshower.cloud.studio.home.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.Route;
import io.sunshower.cloud.studio.workflows.WorkflowDescriptor;
import io.sunshower.cloud.studio.workflows.WorkflowService;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import io.sunshower.zephyr.ui.i18n.Localize;
import io.sunshower.zephyr.ui.i18n.Localized;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.val;

@PermitAll
@Localize
@Breadcrumb(name = "Get Started")
@Route(value = "start", layout = MainView.class)
public class HomeView extends VerticalLayout {

  @Localized private H1 header;

  @Localized private H2 description;

  private final WorkflowService workflowService;

  @Inject
  public HomeView(@NonNull WorkflowService workflowService) {
    getElement().getClassList().add("cta");
    this.workflowService = workflowService;

    header = new H1();
    add(header);

    description = new H2();
    add(description);

    val grid = new Grid<WorkflowDescriptor>();
    grid.setItems(new ListDataProvider<>(workflowService.getWorkflows()));

    grid.addColumn(new ComponentRenderer<>(HorizontalLayout::new, new WorkflowLayout()));
    add(grid);
  }

  static final class WorkflowLayout
      implements SerializableBiConsumer<HorizontalLayout, WorkflowDescriptor> {

    @Override
    public void accept(HorizontalLayout layout, WorkflowDescriptor workflowDescriptor) {

      val img = new Image(workflowDescriptor.moduleIconPath(), workflowDescriptor.name());
      img.setWidth("32px");
      img.setHeight("32px");
      img.getStyle().set("align-self", "center");
      layout.add(img);

      val rhs = new VerticalLayout();
      rhs.setPadding(false);
      rhs.setSpacing(false);
      val name = new Span(workflowDescriptor.name());
      name.getStyle().set("font-size", "var(--lumo-font-size-m)");
      name.getStyle().set("font-weight", "400");
      rhs.add(name);

      val description = new Span(workflowDescriptor.description());
      description.getStyle().set("font-size", "var(--lumo-font-size-s)");
      rhs.add(description);
      layout.add(rhs);

      layout.addClickListener(
          click -> {
            UI.getCurrent().navigate(workflowDescriptor.initialRoute());
          });
    }
  }
}
