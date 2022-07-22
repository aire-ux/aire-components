package io.sunshower.zephyr.security.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.crypt.core.SecretService;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.ui.components.AbstractWizardPage;
import io.sunshower.zephyr.ui.components.ClipboardCopier;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.SplitPanel;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.WizardModelSupport;
import io.sunshower.zephyr.ui.components.WizardPage;
import io.sunshower.zephyr.ui.i18n.Localize;
import io.sunshower.zephyr.ui.i18n.Localized;
import io.sunshower.zephyr.ui.identicon.Identicon;
import javax.inject.Inject;
import lombok.val;

@Localize
@WizardPage(key = "security-info", title = "Seed Security Information")
public class AireSecurityPage extends AbstractWizardPage<String, SecurityInitializationModel>
    implements WizardModelSupport<String, SecurityInitializationModel> {

  //  private final SplitPanel contents;
  private final SecretService secretService;
  private SecurityInitializationModel model;

  @Localized("description.header")
  private H1 header;

  @Localized("description.first")
  private Paragraph descriptionBody;

  @Localized("description.second")
  private Paragraph descriptionBody1;

  @Localized("description.third")
  private Paragraph descriptionBody2;

  @Localized("field.header")
  private H1 rightHeader;

  @Localized("actions.copy-success")
  private String successfullyCopiedValue;

  @Localized("actions.copy-failure")
  private String failedToCopyValue;

  private Image identicon;
  private FormLayout form;
  private SplitPanel content;

  @Inject
  public AireSecurityPage(final SecretService secretService) {
    this.secretService = secretService;
  }

  @Override
  protected Component createContent() {
    content = new SplitPanel();
    content.getElement().setAttribute("slot", "content");

    createLeftPanel(content);
    createRightPanel(content);
    return content;
  }

  private void createRightPanel(SplitPanel result) {
    val rightPanel = new Panel();
    form = new FormLayout();

    rightHeader = new H1();
    rightPanel.add(rightHeader);
    rightPanel.add(form);
    result.setSecond(rightPanel);
    //    result.add(rightPanel);
  }

  private void createLeftPanel(SplitPanel result) {
    val leftPanel = new Panel();
    header = new H1();
    leftPanel.add(header);

    descriptionBody = new Paragraph();
    leftPanel.add(descriptionBody);
    result.setFirst(leftPanel);
    descriptionBody1 = new Paragraph();
    leftPanel.add(descriptionBody1);

    descriptionBody2 = new Paragraph();
    leftPanel.add(descriptionBody2);
  }

  public void onEntered(Wizard<String, SecurityInitializationModel> host) {
    super.onEntered(host);
    this.model = host.getModel();
    getUI()
        .ifPresent(
            ui -> {
              ((Panel) content.getSecond()).remove(form);
              form = new FormLayout();

              val parameters = model.getInitialParameters();
              val img = Identicon.createFromObject(parameters, "Encoded Initialization Parameters");
              form.setWidth("100%");
              form.setResponsiveSteps(new ResponsiveStep("0", 1));
              form.addFormItem(img, "Identicon");
              form.setColspan(img, 1);
              val textField = new TextField();
              textField.setValue(parameters);
              //      form.addFormItem(textField, "Security Init Key");

              val group = new Div();
              group.add(textField);

              val button = new Button(VaadinIcon.COPY.create());
              button.addClickListener(
                  click -> {
                    getUI()
                        .ifPresent(
                            u -> {
                              ClipboardCopier.copy(u, textField::getValue)
                                  .then(
                                      resultHandler -> {
                                        Notification.show(
                                            successfullyCopiedValue, 1000, Position.TOP_STRETCH);
                                      },
                                      errorHandler -> {
                                        Notification.show(
                                            failedToCopyValue, 1000, Position.TOP_STRETCH);
                                      });
                            });
                  });
              group.add(button);
              form.addFormItem(group, "Security Init Params");
              form.setColspan(group, 1);
              ((Panel) content.getSecond()).add(form);
            });
  }

  @Override
  protected Header createHeader() {
    return null;
  }
}
