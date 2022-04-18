package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.RootElement;
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
@SuppressWarnings("PMD")
public class Selector {

  @Attribute private String tagName;

  @Attribute private String selector;
}
