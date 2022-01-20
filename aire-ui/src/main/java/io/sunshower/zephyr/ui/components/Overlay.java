package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

@Tag("aire-overlay")
@JsModule("./aire/ui/components/overlay.ts")
@CssImport("./styles/aire/ui/components/overlay.css")
public abstract class Overlay extends HtmlContainer {

  /**
   * the header for this overlay
   */
  @Getter
  @NonNull
  private Header header;

  /**
   * the footer for this overlay
   */
  @Getter
  @NonNull
  private Footer footer;
  /**
   * the content for this overlay
   */
  @Getter
  @NonNull
  private Article content;

  protected Overlay() {
    header = createHeader();
    content = createContent();
    footer = createFooter();
    add(header);
    add(content);
    add(footer);
  }


  public void addContent(Component... components) {
    getContent().add(components);
  }

  protected Button getCloseButton() {
    val button = new Button(VaadinIcon.CLOSE.create());
    button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
    button.addClassName("aire-overlay-close");
    button.getStyle().set("margin-left", "auto");
    button.addClickListener(event -> close());
    return button;
  }


  public void cancel() {
    getParent().ifPresent(parent -> parent.getElement().removeChild(getElement()));
    this.fireEvent(new OverlayClosedEvent(this, true));
  }

  public void close() {
    getParent().ifPresent(parent -> parent.getElement().removeChild(getElement()));
    this.fireEvent(new OverlayClosedEvent(this, false));
  }

  public Registration addOverlayClosedEventListener(
      ComponentEventListener<OverlayClosedEvent> eventComponentEventListener) {
    return addListener(OverlayClosedEvent.class, eventComponentEventListener);
  }

  protected Header createHeader() {
    val header = new Header();
    header.getElement().setAttribute("slot", "header");
    return header;
  }

  protected Footer createFooter() {
    val footer = new Footer();
    footer.getElement().setAttribute("slot", "footer");
    return footer;
  }


  protected Article createContent() {
    val content = new Article();
    content.getElement().setAttribute("slot", "content");
    return content;
  }
}
