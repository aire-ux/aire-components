package io.sunshower.cloud.studio.git;

import static org.springframework.util.FileSystemUtils.deleteRecursively;

import io.sunshower.cloud.studio.Document;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.persistence.id.Identifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
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
  public void delete(DocumentDescriptor descriptor) {
    val documentId = descriptor.getId().toString() + "." + descriptor.getExtension();
    val file = new File(root, documentId);
    if (file.exists()) {
      deleteRecursively(file);
    }
    this.descriptor.remove(descriptor.getId());
    host.flush();
  }

  @Override
  public Document getOrCreate(DocumentDescriptor descriptor) {
    try {
      val file = registerDescriptor(descriptor);
      return new FileDocument(host.getOwner(), file, storage, descriptor).checkout("main");
    } catch (IOException | GitAPIException ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public Document getOrCreate(DocumentDescriptor descriptor, String branch) {
    if(!this.descriptor.contains(descriptor.getId())) {
      this.descriptor.addDocument(descriptor);
    }
    try {
      val file = registerDescriptor(descriptor);
      return new FileDocument(host.getOwner(), file, storage, descriptor).checkout(branch);
    } catch (IOException | GitAPIException ex) {
      throw new WorkspaceException(ex);
    }
  }

  private File registerDescriptor(DocumentDescriptor descriptor)
      throws IOException, GitAPIException {
    if(!this.descriptor.contains(descriptor.getId())) {
      this.descriptor.addDocument(descriptor);
    }
    val documentId = descriptor.getId().toString() + "." + descriptor.getExtension();
    val file = new File(root, documentId);
    val path = file.toPath();
    if (!file.exists()) {
      Files.createFile(path);
    }
    storage.add().addFilepattern(documentId).call();
    return file;
  }

  @Override
  @NonNull
  public List<DocumentDescriptor> getDocuments() {
    return descriptor.getAllDocuments();
  }

  @Override
  public DocumentDescriptor getDocumentDescriptor(Identifier id) {
    return descriptor.getDocument(id);
  }
}
