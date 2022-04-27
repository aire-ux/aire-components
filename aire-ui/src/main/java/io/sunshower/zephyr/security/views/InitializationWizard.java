package io.sunshower.zephyr.security.views;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.templatemodel.TemplateModel;
public class InitializationWizard extends LoginForm {

  public interface Model extends TemplateModel {

    void setError(boolean error);
  }
}
