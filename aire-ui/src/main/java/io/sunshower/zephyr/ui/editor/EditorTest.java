package io.sunshower.zephyr.ui.editor;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.sunshower.zephyr.MainView;
import io.sunshower.zephyr.ui.controls.Breadcrumb;
import javax.annotation.security.PermitAll;

@PermitAll
@Route(value = "editor/test", layout = MainView.class)
@Breadcrumb(name = "Topology", host = MainView.class)
public class EditorTest extends VerticalLayout {

  private Editor editor;

  public EditorTest() {
    editor = new Editor();
    add(editor);
  }


}
