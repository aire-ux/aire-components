package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("aire-form-panel")
@JsModule("./aire/ui/components/form-panel.ts")
@CssImport("./styles/aire/components/form-panel.css")
public class FormPanel extends Component implements HasComponents {}
