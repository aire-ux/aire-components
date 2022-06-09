package io.sunshower.zephyr.ui.editor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import io.sunshower.zephyr.ui.canvas.Model;
import io.sunshower.zephyr.ui.canvas.RemoteAction;
import io.sunshower.zephyr.ui.editor.Constants.Versions;
import io.sunshower.zephyr.ui.rmi.ClientMethods;
import io.sunshower.zephyr.ui.rmi.ClientResult;

@Tag("aire-editor")
@JsModule("./aire/ui/editor/aire-editor.ts")
@CssImport("./styles/aire/ui/editor/editor.css")
@JsModule("@aire-ux/aire-condensation/dist/index.js")
@NpmPackage(value = "@aire-ux/aire-condensation", version = "0.1.5")
// @NpmPackage(value = "@sunshower/breeze-lang", version = "0.0.0")
@NpmPackage(value = "@codemirror/view", version = Versions.View)
@NpmPackage(value = "@codemirror/lint", version = Versions.Lint)
@NpmPackage(value = "@codemirror/state", version = Versions.State)
@NpmPackage(value = "@codemirror/language", version = Versions.Language)
@NpmPackage(value = "@codemirror/commands", version = Versions.Commands)
@NpmPackage(value = "@codemirror/search", version = Versions.Search)
@NpmPackage(value = "@codemirror/autocomplete", version = Versions.Autocomplete)
@NpmPackage(value = "@codemirror/language-data", version = Versions.LanguageData)
@NpmPackage(value = "@codemirror/basic-setup", version = Versions.BasicSetup)
public class Editor extends Component {


  private EditorState editorState;

  public Editor() {
    editorState = new DefaultEditorState(this);
  }


  public <T> ClientResult<T> invoke(
      Class<? extends RemoteAction<T, EditorState>> action, Object... arguments) {
    return ClientMethods.withUiSupplier(this).construct(action, arguments).apply(getState());
  }


  public EditorState getState() {
    return editorState;
  }
}
