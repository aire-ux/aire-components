package io.sunshower.zephyr.condensation;

import io.sunshower.arcus.condensation.Convert;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;

@RootElement
public class StringValue {

  @Element
  @Convert(StringValueConverter.class)
  private String value;

  public StringValue(String value) {
    this.value = value;
  }
}
