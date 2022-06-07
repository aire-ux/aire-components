package io.sunshower.cloud.studio;

import java.io.InputStream;
import java.util.List;

public interface Document {

  Document checkout();

  Document checkout(String branchName);

  InputStream read();

  void write(InputStream source);

  Revision commit(String commitMessage);

  void checkout(Revision revision);

  List<Revision> getRevisions();
}
