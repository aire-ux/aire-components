package io.sunshower.zephyr.security.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.security.views.UserInfoPage.IF;
import io.sunshower.zephyr.ui.components.AbstractWizardPage;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.SplitPanel;
import io.sunshower.zephyr.ui.components.WizardPage;
import io.sunshower.zephyr.ui.i18n.Localize;
import io.sunshower.zephyr.ui.i18n.Localized;
import lombok.val;

@Localize
@WizardPage(key = "user-info-page", title = "Administrator Info", iconFactory = IF.class)
public class UserInfoPage extends AbstractWizardPage<String, SecurityInitializationModel> {


  private SplitPanel content;
  private SecurityInitializationModel model;


  /**
   * localized content
   */
  @Localized("title.key")
  private H1 header;

  @Localized("description.first")
  private Paragraph p1;

  @Localized("description.realm")
  private Paragraph p2;



  @Localized("description.realm.2")
  private Paragraph p3;

  @Localized("description.realm.3")
  private Paragraph p4;


  /**
   * labels
   */
  @Localized("labels.firstname")
  private String firstNameLabel;

  @Localized("labels.lastname")
  private String lastnameLabel;

  @Localized("labels.emailaddress")
  private String emailAddressLabel;

  @Localized("labels.password")
  private String passwordLabel;


  @Localized("labels.passwordConfirm")
  private String confirmPasswordLabel;
  private TextField firstNameField;
  private TextField lastNameField;
  private EmailField emailAddressField;
  private PasswordField passwordField;
  private PasswordField passwordConfirmField;


  @Override
  protected Component createContent() {

    content = new SplitPanel();
    content.getElement().setAttribute("slot", "content");

    createLeftPanel(content);
    return content;
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    createRightPanel(content);
  }

  private void createRightPanel(SplitPanel content) {

    val formView = new FormLayout();


    firstNameField = new TextField(firstNameLabel);
    lastNameField = new TextField(lastnameLabel);
    emailAddressField = new EmailField(emailAddressLabel);
    passwordField = new PasswordField(passwordLabel);
    passwordConfirmField = new PasswordField(confirmPasswordLabel);

    formView.add(firstNameField, 1);
    formView.add(lastNameField, 1);
    formView.add(emailAddressField, 2);
    formView.add(passwordField, 1);
    formView.add(passwordConfirmField, 1);

    content.setSecond(formView);


  }

  private void createLeftPanel(SplitPanel content) {
    header = new H1();
    val leftPanel = new Panel();
    content.setFirst(leftPanel);
    leftPanel.add(header);
    p1 = new Paragraph();
    p2 = new Paragraph();
    p3 = new Paragraph();
    p4 = new Paragraph();
    leftPanel.add(p1, p2, p3, p4);
  }

  public static class IF implements IconFactory {

    @Override
    public Icon create() {
      return VaadinIcon.LOCK.create();
    }
  }
}
