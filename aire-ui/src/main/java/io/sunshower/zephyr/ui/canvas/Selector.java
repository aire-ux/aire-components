package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Attribute;
import com.aire.ux.condensation.RootElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@RootElement
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Selector {

  @Attribute
  private String tagName;

  @Attribute
  private String selector;

}
