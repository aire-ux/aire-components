package io.sunshower.zephyr.ui.canvas;

import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import lombok.Getter;
import lombok.Setter;

@RootElement
public class EdgeTemplate extends AbstractCellTemplate {

  @Getter @Setter @Element private Connector connector;
}
