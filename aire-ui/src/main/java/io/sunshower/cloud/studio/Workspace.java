package io.sunshower.cloud.studio;

import io.sunshower.persistence.id.Identifier;
import java.util.List;

public interface Workspace {

  void delete();

  void delete(DocumentDescriptor descriptor);

  Document getOrCreate(DocumentDescriptor documentDescriptor);

  Document getOrCreate(DocumentDescriptor descriptor, String branch);

  List<DocumentDescriptor> getDocuments();

  DocumentDescriptor getDocumentDescriptor(Identifier id);
}
