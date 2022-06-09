package io.sunshower.cloud.studio;

import io.sunshower.arcus.condensation.Attribute;
import io.sunshower.arcus.condensation.Convert;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.model.api.IdentifierConverter;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@RootElement
@ToString
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
  @Convert(key = IdentifierConverter.class)
  private Map<Identifier, DocumentDescriptor> documents;

  @Getter
  @Setter
  @Attribute
  private String name;

  @Getter
  @Setter
  @Element
  private String description;

  public WorkspaceDescriptor() {
    id = sequence.next();
    documents = new HashMap<>();
  }

  @NonNull
  public List<DocumentDescriptor> getAllDocuments() {
    return List.copyOf(documents.values());
  }

  @Nullable
  public DocumentDescriptor getDocument(@NonNull Identifier id) {
    return documents.get(id);
  }


  public boolean contains(@NonNull Identifier id) {
    return documents.containsKey(id);
  }

  public void addDocument(DocumentDescriptor descriptor) {
    documents.put(descriptor.getId(), descriptor);
  }

  public void remove(Identifier id) {
    documents.remove(id);
  }
}
