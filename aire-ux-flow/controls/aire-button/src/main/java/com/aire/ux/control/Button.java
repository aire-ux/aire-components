package com.aire.ux.control;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

@Tag("aire-button")
@JsModule("@aire-ux/aire-button")
@NpmPackage(value = "@aire-ux/aire-button", version = "0.0.14")
public class Button extends Component implements HasStyle {}
