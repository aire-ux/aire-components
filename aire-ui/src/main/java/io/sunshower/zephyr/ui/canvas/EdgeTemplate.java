package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import lombok.Getter;
import lombok.Setter;

@RootElement
public class EdgeTemplate extends AbstractCellTemplate {

  @Getter @Setter @Element private Connector connector;
}
