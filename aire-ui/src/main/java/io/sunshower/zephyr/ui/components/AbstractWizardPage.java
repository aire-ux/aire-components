package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableTriConsumer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

/**
 * base-class for commonly-themed wizard-pages
 */
@Tag("aire-wizard-page")
@JsModule("./aire/ui/components/wizard-page.ts")
@CssImport("./styles/aire/ui/components/wizard-page.css")
public class AbstractWizardPage<K, T> extends Component implements HasComponents,
    WizardModelSupport<K, T> {

  /**
   * the wizard-key associated with this page
   */
  private final K key;

  /**
   * the model-type associated with this page
   */
  private final Class<T> modelType;

  /**
   * the header for this page.  May be overridden by overriding {@code createHeader()}
   */
  @Getter(AccessLevel.PROTECTED)
  private final Component header;
  /**
   * the footer for this page.  May be overridden by overriding {@code createFooter()}.
   */
  @Getter(AccessLevel.PROTECTED)
  private final Component footer;


  /**
   * the content for this page.  May be overridden by overridding  {@code createContent()}
   */
  @Getter(AccessLevel.PROTECTED)
  private final Component content;

  /**
   * the list of controls.  Defaults to a previous button and a next button
   */
  private final List<ControlDefinition<? super Component, K, T>> controls;
  /**
   * the wizard associated with this page.  May be null depending on the component lifecycle if you
   * need a non-null wizard at any point, use @Dynamic on your subclass's constructor and the
   * relevant constructor argument
   */
  private Wizard<K> wizard;

  protected AbstractWizardPage(@NonNull K key, @NonNull Class<T> modelType) {
    this.key = key;
    this.modelType = modelType;
    this.header = createHeader();
    this.content = createContent();
    this.footer = createFooter();
    this.controls = new ArrayList<>();
    add(header, content, footer);
  }

  @SuppressWarnings("unchecked")
  protected AbstractWizardPage(Class<T> modelType) {
    val descriptor = read((Class<? extends AbstractWizardPage<K, T>>) getClass());
    this.key = (K) descriptor.key;
    this.modelType = modelType;
    this.header = createHeader();
    this.content = createContent();
    this.footer = createFooter();
    this.controls = new ArrayList<>();
    setTitle(descriptor.title);
    add(header, content, footer);
  }


  public void addContent(Component... contents) {
    if (content instanceof HasComponents) {
      ((HasComponents) content).add(contents);
    } else {
      Arrays.stream(contents).forEachOrdered(c -> content.getElement().appendChild(c.getElement()));
    }
  }


  @Override
  public boolean addModelElement(T value) {
    return false;
  }

  @Override
  public <V extends Component> boolean transitionTo(Class<V> type) {
    return wizard.transitionTo(type);
  }

  @Override
  public boolean transitionTo(K key) {
    return wizard.transitionTo(key);
  }

  public boolean advance() {
    if (wizard.canAdvance()) {
      wizard.advance();
      return true;
    }
    return false;
  }

  public boolean retreat() {
    if (wizard.canRetreat()) {
      wizard.retreat();
      return true;
    }
    return false;
  }


  @Override
  public void onEntered(Wizard<K> wizard) {
    this.wizard = wizard;
    for (val definition : controls) {
      definition.f.accept((Component) definition.control, this, wizard);
    }
  }

  @Override
  public void onExited(Wizard<K> wizard) {
  }


  @SuppressWarnings("unchecked")
  protected <U extends Component> void addNavigationControl(
      U control, SerializableTriConsumer<U, WizardModelSupport<K, T>, Wizard<K>> f) {
    controls.add(new ControlDefinition<>(control,
        (SerializableTriConsumer<Component, WizardModelSupport<K, T>, Wizard<K>>) f));
    footer.getElement().appendChild(control.getElement());
  }


  protected Header createHeader() {
    val header = new Header();
    header.getElement().setAttribute("slot", "header");
    return header;
  }

  protected Component createContent() {
    val content = new VerticalLayout();
    content.getElement().setAttribute("slot", "content");
    return content;
  }

  protected Component createFooter() {
    val footer = new Footer();
    footer.getElement().setAttribute("slot", "footer");
    return footer;
  }

  protected void setTitle(String title) {
    setTitle(new Text(title));
  }

  protected void setTitle(Component title) {
    header.getElement().appendChild(title.getElement());
  }

  protected void setModelElement(T value) {
    Objects.requireNonNull(wizard).getModel().set(getKey(), value);
  }


  protected K getKey() {
    return key;
  }

  @SuppressWarnings("unchecked")
  private WizardPageDescriptor read(Class<? extends AbstractWizardPage<K, T>> type) {
    val wizardPage = type.getAnnotation(WizardPage.class);
    if (wizardPage == null) {
      return new WizardPageDescriptor(type.getCanonicalName(), wizardPage.title());
    } else {
      return new WizardPageDescriptor(wizardPage.key(), wizardPage.title());
    }
  }


  private static class WizardPageDescriptor {

    private final String key;
    private final String title;

    public WizardPageDescriptor(String key, String title) {
      this.key = key;
      this.title = title;
    }
  }

  record ControlDefinition<U, K, T>(U control,
                                    SerializableTriConsumer<U, WizardModelSupport<K, T>, Wizard<K>> f) {

  }
}
