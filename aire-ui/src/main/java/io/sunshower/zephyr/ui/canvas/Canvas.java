package io.sunshower.zephyr.ui.canvas;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

@Tag("aire-canvas")
@JsModule("@antv/x6/dist/x6.js")
@JsModule("./aire/ui/canvas/canvas.ts")
@NpmPackage(value = "@antv/x6", version = "1.29.6")
public class Canvas extends HtmlContainer {

}
