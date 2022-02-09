package io.sunshower.zephyr.ui.canvas.listeners;

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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Location {

  @Attribute
  private Double x;

  @Attribute
  private Double y;

}
