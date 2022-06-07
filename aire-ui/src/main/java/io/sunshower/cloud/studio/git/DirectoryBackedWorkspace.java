package io.sunshower.cloud.studio.git;

import io.sunshower.cloud.studio.Document;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.cloud.studio.WorkspaceManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class DirectoryBackedWorkspace implements Workspace {

  @Getter private final File root;
  private final Git storage;
  private final WorkspaceManager host;
  private final WorkspaceDescriptor descriptor;

  public DirectoryBackedWorkspace(
      @NonNull Git storage,
      @NonNull File root,
      @NonNull WorkspaceManager host,
      @NonNull WorkspaceDescriptor descriptor) {
    this.host = host;
    this.root = root;
    this.storage = storage;
    this.descriptor = descriptor;
  }

  @Override
  public void delete() {
    host.delete(descriptor);
  }

  @Override
  public void delete(DocumentDescriptor descriptor) {}

  @Override
  public Document getOrCreate(DocumentDescriptor descriptor) {
    try {
      val documentId = descriptor.getId().toString() + "." + descriptor.getExtension();
      val file = new File(root, documentId);
      val path = file.toPath();
      if (!file.exists()) {
        Files.createFile(path);
      }
      storage.add().addFilepattern(documentId).call();
      return new FileDocument(host.getOwner(), file, storage, descriptor).checkout("main");
    } catch (IOException | GitAPIException ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public Document getOrCreate(DocumentDescriptor descriptor, String branch) {
    try {
      val documentId = descriptor.getId().toString() + "." + descriptor.getExtension();
      val file = new File(root, documentId);
      val path = file.toPath();
      if (!file.exists()) {
        Files.createFile(path);
      }
      storage.add().addFilepattern(documentId).call();
      return new FileDocument(host.getOwner(), file, storage, descriptor).checkout(branch);
    } catch (IOException | GitAPIException ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public List<DocumentDescriptor> getDocuments() {
    return null;
  }
}
