package com.aire.ux.annotations.ext.scenario1;

import com.aire.ux.Host;
import com.aire.ux.Slot;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import java.util.Objects;
import lombok.Getter;

@Route("home")
@Host("test-extension-point")
public class TestExtensionPoint extends VerticalLayout implements RouterLayout {

  @Slot(":head")
  private Header header;


  @Getter
  @Slot(":content")
  private Section content;

  public TestExtensionPoint() {
    header = new Header();
    add(header);
    content = new Section();
    add(content);
  }

  public void showRouterLayoutContent(HasElement content) {
    if (content != null) {
      content.getElement()
          .appendChild(Objects.requireNonNull(content.getElement()));
    }
  }

  public void removeRouterLayoutContent(HasElement oldContent) {
    oldContent.getElement().removeFromParent();
  }

}
