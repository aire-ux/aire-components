package io.sunshower.zephyr.condensation;

import elemental.json.impl.JsonUtil;
import io.sunshower.arcus.condensation.Converter;

public class StringValueConverter implements Converter<String, String> {

  @Override
  public String read(String s) {
    if (s == null) {
      return null;
    }
    return JsonUtil.parse(s).asString();
  }

  @Override
  public String write(String s) {
    if (s == null) {
      return null;
    }
    return JsonUtil.quote(s);
  }
}
