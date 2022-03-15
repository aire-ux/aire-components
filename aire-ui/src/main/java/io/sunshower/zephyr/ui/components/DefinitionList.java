package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;

@Tag("dl")
@CssImport("./styles/aire/ui/components/definition-list.css")
public class DefinitionList extends HtmlContainer {

  public DefinitionList() {

  }

  public DefinitionTitleBuilder key(Component key) {
    return new DefinitionTitleBuilder(new Label(key));
  }

  public DefinitionTitleBuilder key(String text) {
    return key(new Label(text));
  }


  @Tag("dt")
  public static class Label extends Component implements HasText {

    /**
     * Creates an instance using the given text.
     *
     * @param text the text to show, <code>null</code> is interpreted as an empty string
     */
    public Label(String text) {
      getElement().setText(text);
    }

    public Label(Component component) {
      getElement().appendChild(component.getElement());
    }
  }

  @Tag("dd")
  public static class Description extends Component implements HasText {

    /**
     * Creates an instance using the given text.
     *
     * @param text the text to show, <code>null</code> is interpreted as an empty string
     */
    public Description(String text) {
      getElement().setText(text);
    }

    public Description(Component component) {
      getElement().appendChild(component.getElement());
    }
  }


  public class DefinitionTitleBuilder {

    final Component key;

    public DefinitionTitleBuilder(Component key) {
      this.key = key;
    }

    public DefinitionList value(String value) {
      return value(new Description(value));
    }

    public DefinitionList value(Component value) {
      DefinitionList.this.add(key, new Description(value));
      return DefinitionList.this;
    }


  }
}
