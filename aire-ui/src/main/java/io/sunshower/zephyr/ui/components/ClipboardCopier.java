package io.sunshower.zephyr.ui.components;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.PendingJavaScriptResult;
import java.util.function.Supplier;

public class ClipboardCopier {

  static final String fn = "(function doCopy(text) {\n"
                           + "  if (!navigator.clipboard) {\n"
                           + "    var textArea = document.createElement(\"textarea\");\n"
                           + "    textArea.value = text;\n"
                           + "    textArea.style.top = \"0\";\n"
                           + "    textArea.style.left = \"0\";\n"
                           + "    textArea.style.position = \"fixed\";\n"
                           + "\n"
                           + "    document.body.appendChild(textArea);\n"
                           + "    textArea.focus();\n"
                           + "    textArea.select();\n"
                           + "    try {\n"
                           + "      var successful = document.execCommand('copy');\n"
                           + "      var msg = successful ? 'successful' : 'unsuccessful';\n"
                           + "      console.log('Fallback: Copying text command was ' + msg);\n"
                           + "    } catch (err) {\n"
                           + "      console.error('Fallback: Oops, unable to copy', err);\n"
                           + "    }\n"
                           + "    document.body.removeChild(textArea);\n"
                           + "  } else {\n"
                           + "    navigator.clipboard.writeText(text).then(function () {\n"
                           + "      console.log('Async: Copying to clipboard was successful!');\n"
                           + "    }, function (err) {\n"
                           + "      console.error('Async: Could not copy text: ', err);\n"
                           + "    });\n"
                           + "  }\n"
                           + "})($0);";


  public static PendingJavaScriptResult copy(UI ui, HasText target) {
    return ui.getPage().executeJs(fn, target.getText());
  }

  public static PendingJavaScriptResult copy(UI ui, Supplier<String> target) {
    return ui.getPage().executeJs(fn, target.get());
  }

  public static PendingJavaScriptResult copy(UI ui, String target) {
    return ui.getPage().executeJs(fn, target);
  }

  public static PendingJavaScriptResult copy(
      UI ui, HasValue<ValueChangeEvent<String>, String> target) {
    return ui.getPage().executeJs(fn, target.getValue());
  }
}
