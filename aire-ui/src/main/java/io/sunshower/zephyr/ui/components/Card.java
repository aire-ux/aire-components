package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Section;
import lombok.NonNull;
import lombok.val;

@Tag("aire-card")
@JsModule("./aire/ui/components/card.ts")
@CssImport("./styles/aire/ui/components/card.css")
public class Card extends HtmlContainer {

  public static final String SLOT = "slot";
  public static final String FOOTER = "footer";
  public static final String CONTENT = "content";
  public static final String HEADER = "header";
  public static final String ICON = "icon";
  /** the header of this card */
  private final Component header;

  /** the content of this card */
  private final Component content;

  /** the footer of this card */
  private final Component footer;

  public Card() {
    add(header = createHeader());
    add(content = createContent());
    add(footer = createFooter());
  }

  @NonNull
  public Component getHeader() {
    return header;
  }

  @NonNull
  public Component getContent() {
    return content;
  }

  @NonNull
  public Component getFooter() {
    return footer;
  }

  public void remove(Slot slot, Component... components) {
    val component = get(slot);
    for (val child : components) {
      component.getElement().removeChild(child.getElement());
    }
  }

  public void add(Slot slot, Component... components) {
    val component = get(slot);
    for (val child : components) {
      component.getElement().appendChild(child.getElement());
    }
  }

  public Component get(Slot slot) {
    return switch (slot) {
      case Footer -> footer;
      case Header -> header;
      case Content -> content;
    };
  }

  public void setIcon(Component icon) {
    icon.getElement().setAttribute(SLOT, ICON);
    add(icon);
  }

  protected Component createHeader() {
    val header = new Header();
    header.getElement().setAttribute(SLOT, HEADER);
    return header;
  }

  protected Component createContent() {
    val section = new Section();
    section.getElement().setAttribute(SLOT, CONTENT);
    return section;
  }

  protected Component createFooter() {
    val footer = new Footer();
    footer.getElement().setAttribute(SLOT, FOOTER);
    return footer;
  }

  public enum Slot {
    Header,
    Content,
    Footer
  }
}
