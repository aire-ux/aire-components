package io.sunshower.cloud.studio;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Convert;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.model.api.IdentifierConverter;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@RootElement
@EqualsAndHashCode
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

  @Getter
  @Setter
  @Element
  @Convert(key = IdentifierConverter.class)
  private Map<Identifier, DocumentDescriptor> documents;

  @Getter @Setter @Attribute private String name;

  @Getter @Setter @Element private String description;

  public WorkspaceDescriptor() {
    id = sequence.next();
  }

  @NonNull
  public List<DocumentDescriptor> getAllDocuments() {
    return documents == null ? Collections.emptyList() : List.copyOf(documents.values());
  }

  @Nullable
  public DocumentDescriptor getDocument(@NonNull Identifier id) {
    if (documents == null) {
      return null;
    }
    return documents.get(id);
  }

  public boolean contains(@NonNull Identifier id) {
    if (documents == null) {
      return false;
    }
    return documents.containsKey(id);
  }

  public void addDocument(DocumentDescriptor descriptor) {
    if (documents == null) {
      documents = new HashMap<>();
    }
    documents.put(descriptor.getId(), descriptor);
  }

  public void remove(Identifier id) {
    if (documents == null) {
      return;
    }
    documents.remove(id);
  }
}
