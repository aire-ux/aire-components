package com.aire.ux.theme.context;

import com.aire.ux.Theme;
import com.aire.ux.theme.BaseTheme;

/**
 * theme that contains no resources
 */
@SuppressWarnings("PMD")
public class EmptyTheme extends BaseTheme {


  static final String id = "aire-empty-theme";
  public EmptyTheme() {
    super(id, EmptyTheme.class.getClassLoader());
  }

  public static Theme getInstance() {
    return Holder.instance;
  }

  static final class Holder {
    static final Theme instance = new EmptyTheme();
  }
}
