package io.sunshower.zephyr.ui.canvas;

import com.aire.ux.condensation.Element;
import com.aire.ux.condensation.RootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RootElement
@EqualsAndHashCode
public class VertexTemplateAttributes {

  @Element private VertexAttribute body;
}
