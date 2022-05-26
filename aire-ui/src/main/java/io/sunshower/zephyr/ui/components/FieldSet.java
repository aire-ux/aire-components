package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("aire-fieldset")
@JsModule("./aire/ui/components/fieldset.ts")
@CssImport("./styles/aire/ui/components/fieldset.css")
public class FieldSet extends Component implements HasComponents {

  public FieldSet() {}
}
