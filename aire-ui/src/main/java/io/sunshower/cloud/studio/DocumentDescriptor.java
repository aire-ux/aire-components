package io.sunshower.cloud.studio;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import lombok.Getter;
import lombok.Setter;

@RootElement
public class DocumentDescriptor {

  static final Sequence<Identifier> sequence;

  static {
    sequence = Identifiers.newSequence(true);
  }

  @Getter @Setter @Attribute private String name;
  @Getter @Setter @Attribute private Identifier id;

  @Getter @Setter @Attribute private String extension;

  public DocumentDescriptor() {
    setId(sequence.next());
  }
}
