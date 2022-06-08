package io.sunshower.cloud.studio;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Convert;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.model.api.IdentifierConverter;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import lombok.Getter;
import lombok.Setter;

@RootElement
public class WorkspaceDescriptor {

  static final Sequence<Identifier> sequence;

  static {
    sequence = Identifiers.newSequence(true);
  }

  @Getter
  @Setter
  @Attribute
  @Convert(IdentifierConverter.class)
  private Identifier id;

  @Getter @Setter @Attribute private String name;

  @Getter @Setter @Element private String description;

  public WorkspaceDescriptor() {
    id = sequence.next();
  }
}
