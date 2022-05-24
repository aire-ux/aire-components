package io.sunshower.zephyr.security.views;

import com.aire.ux.actions.Key;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import io.sunshower.model.api.User;
import io.sunshower.model.api.UserDetails;
import io.sunshower.realms.cryptkeeper.FileBackedCryptKeeperRealm;
import io.sunshower.realms.cryptkeeper.RealmConfiguration;
import io.sunshower.zephyr.security.CompositeRealmManager;
import io.sunshower.zephyr.security.model.initialization.SecurityInitializationModel;
import io.sunshower.zephyr.security.views.UserInfoPage.IF;
import io.sunshower.zephyr.ui.components.AbstractWizardPage;
import io.sunshower.zephyr.ui.components.Panel;
import io.sunshower.zephyr.ui.components.SplitPanel;
import io.sunshower.zephyr.ui.components.Wizard;
import io.sunshower.zephyr.ui.components.Wizard.TransitionListener;
import io.sunshower.zephyr.ui.components.WizardPage;
import io.sunshower.zephyr.ui.i18n.Localize;
import io.sunshower.zephyr.ui.i18n.Localized;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;

@Localize
@WizardPage(key = "user-info-page", title = "Administrator Info", iconFactory = IF.class)
public class UserInfoPage extends AbstractWizardPage<String, SecurityInitializationModel> implements
    TransitionListener<String, SecurityInitializationModel> {


  private final File realmDirectory;
  private final Configuration configuration;
  private final CompositeRealmManager realmManager;
  private final Path configurationFile;


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

  @Localized("errors.firstnametooshort")
  private String firstNameLengthMessage;

  @Localized("errors.lastnametooshort")
  private String lastNameLengthMessage;

  @Localized("errors.invalidemail")
  private String emailError;

  @Localized("labels.passwordConfirm")
  private String confirmPasswordLabel;


  private TextField firstNameField;
  private TextField lastNameField;
  private EmailField emailAddressField;
  private PasswordField passwordField;
  private PasswordField passwordConfirmField;


  private Binder<User> userBinder;
  private Binder<UserDetails> userDetailsBinder;

  @Inject
  public UserInfoPage(
      CompositeRealmManager realmManager,
      Configuration configuration,
      @Named("realmDirectory") File realmDirectory,
      @Named("aireConfigurationFile") Path configurationFile
  ) {
    this.realmManager = realmManager;
    this.configuration = configuration;
    this.realmDirectory = realmDirectory;
    this.configurationFile = configurationFile;
  }

  public void onEntered(Wizard<String, SecurityInitializationModel> host) {
    super.onEntered(host);
    this.model = host.getModel();
  }

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
    getControl(Key.of("next")).ifPresent(next -> ((Button) next).setEnabled(false));
  }


  private void createRightPanel(SplitPanel content) {
    val second = content.getSecond();
    if (second != null) {
      return;
    }

    getControl(Key.of("next")).ifPresent(c -> {
      val button = (Button) c;
      button.addClickListener(click -> {
        doSave();
      });
    });

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

    attachListeners();
  }

  private void attachListeners() {
    userBinder = new Binder<>(User.class);

    userBinder.forField(emailAddressField)
        .withValidator(new EmailValidator(emailError))
        .withValidationStatusHandler(this::validate)
        .bind(User::getUsername, User::setUsername);

    userBinder.forField(passwordField)
        .withValidator(
            new StringLengthValidator("Password must be between 4-255 characters", 4, 255))
        .withValidationStatusHandler(this::validate)
        .bind(User::getPassword, User::setPassword);

    userDetailsBinder = new Binder<>(UserDetails.class);
    userDetailsBinder.forField(firstNameField)
        .withValidator(new StringLengthValidator(firstNameLengthMessage, 1, 255))
        .withValidationStatusHandler(this::validate)
        .bind(UserDetails::getFirstName, UserDetails::setFirstName);

    userDetailsBinder.forField(lastNameField)
        .withValidator(new StringLengthValidator(lastNameLengthMessage, 1, 255))
        .withValidationStatusHandler(this::validate)
        .bind(UserDetails::getLastName, UserDetails::setLastName);
  }

  private void doSave() {

    try {
      val user = new User();
      userBinder.writeBean(user);
      user.setUsername(emailAddressField.getValue());
      user.setSalt(model.getRawSalt());
      user.setInitializationVector(model.getRawInitializationVector());

      val details = new UserDetails();
      userDetailsBinder.writeBean(details);
      user.setDetails(details);

      val plaintextPassword = passwordField.getValue();

      val realmConfiguration = new RealmConfiguration(
          realmDirectory,
          plaintextPassword,
          model.getRawSalt(),
          model.getRawInitializationVector()
      );
      val realm = new FileBackedCryptKeeperRealm(realmConfiguration);
      realm.saveUser(user);
      val encoding = Encodings.create(Type.Base58);

      configuration.setProperty("default.realm.iv",
          encoding.encode(realmConfiguration.getInitializationVector()));
      configuration.setProperty("default.realm.salt",
          encoding.encode(realmConfiguration.getSalt()));
      configuration.setProperty("aire.initialized", true);
      saveConfiguration(configuration);
      realmManager.register(realm);
      UI.getCurrent().navigate(AuthenticationView.class);
    } catch (ValidationException ex) {
      Notification.show("Invalid user--not saving.  Try again");
    }
  }

  private void saveConfiguration(Configuration configuration) {
    val layout = new PropertiesConfigurationLayout();

    try (val out = Files.newBufferedWriter(configurationFile, StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE, StandardOpenOption.DSYNC)) {
      layout.save((PropertiesConfiguration) configuration, out);
    } catch (IOException | ConfigurationException ex) {
      Notification.show("Error saving configuration: '%s'".formatted(ex.getMessage()));
    }
  }

  private void validate(BindingValidationStatus<?> bindingValidationStatus) {
    getControl(Key.of("next")).ifPresent(
        u -> ((Button) u).setEnabled(!bindingValidationStatus.isError()));
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
