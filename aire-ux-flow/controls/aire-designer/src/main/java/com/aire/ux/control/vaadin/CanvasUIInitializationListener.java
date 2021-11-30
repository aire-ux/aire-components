package com.aire.ux.control.vaadin;

import com.aire.ux.condensation.Condensation;
import com.aire.ux.control.designer.servlet.DesignerConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.UIInitListener;
import com.vaadin.flow.shared.ui.LoadMode;
import lombok.extern.java.Log;
import lombok.val;

@Log
public class CanvasUIInitializationListener implements
    UIInitListener {

  @Override
  public void uiInit(UIInitEvent event) {
    val ui = event.getUI();
    val servlet = event.getSource();
    val configuration = DesignerConfiguration.getInstance();
    if (configuration.requiresInitialization(ui.getElement())) {
      configure(ui, configuration);
    }
//    servlet.getContext().getContextParameter()

  }

  private void configure(UI ui, DesignerConfiguration configuration) {
    ui.getPage()
        .addJavaScript("context://aire/designer/client/@aire-ux/mxgraph/javascript/dist/build.js",
            LoadMode.EAGER);
//    ui.getPage().executeJs("mxgraph($0)", Condensation.write(configuration.))
//    ui.getPage().executeJs().toCompletableFuture().get();

  }
}
