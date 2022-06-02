package io.sunshower.zephyr.ui.editor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import io.sunshower.zephyr.ui.editor.Constants.Versions;

@Tag("aire-editor")
@JsModule("./aire/ui/editor/aire-editor.ts")
@CssImport("./styles/aire/ui/editor/editor.css")
@NpmPackage(value = "@codemirror/view", version = Versions.View)
@NpmPackage(value = "@codemirror/lint", version = Versions.Lint)
@NpmPackage(value = "@codemirror/state", version = Versions.State)
@NpmPackage(value = "@codemirror/language", version = Versions.Language)
@NpmPackage(value = "@codemirror/commands", version = Versions.Commands)
@NpmPackage(value = "@codemirror/search", version = Versions.Search)
@NpmPackage(value = "@codemirror/autocomplete", version = Versions.Autocomplete)
@NpmPackage(value = "@codemirror/language-data", version = Versions.LanguageData)
@NpmPackage(value = "@codemirror/basic-setup", version = Versions.BasicSetup)
public class Editor extends Component {}
