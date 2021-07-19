package com.aire.ux.theme.material;

import com.aire.ux.control.Button;
import com.aire.ux.theme.BaseTheme;
import com.aire.ux.theme.JavascriptResource;
import com.aire.ux.theme.StylesheetResource;

@SuppressWarnings("PMD.UseProperClassLoader")
public class BaseMaterialTheme extends BaseTheme {

  protected BaseMaterialTheme(final String id, final String baseStyleSheet) {
    super(id, BaseMaterialTheme.class.getClassLoader());
    addResource(
        new StylesheetResource(
            "bootstrap.css", "META-INF/aire-theme/aire-material/" + baseStyleSheet));

    addResource(new JavascriptResource(
        "mdb.js",
        "META-INF/aire-theme/aire-material/mdb.min.js"
    ));
    register(Button.class, MaterialButtonDecorator.class);

  }
}
