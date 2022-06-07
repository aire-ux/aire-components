package io.sunshower.cloud.studio.git;

import io.sunshower.cloud.studio.Document;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Revision;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.model.api.User;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.WillNotClose;
import lombok.NonNull;
import lombok.val;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;

public class FileDocument implements Document {

  private final File file;
  private final User owner;
  private final Git storage;
  private final DocumentDescriptor descriptor;

  public FileDocument(
      @NonNull User user,
      @NonNull File file,
      @NonNull Git storage,
      @NonNull DocumentDescriptor documentDescriptor) {
    this.file = file;
    this.storage = storage;
    this.owner = user;

    this.descriptor = documentDescriptor;
    try {
      val ref = storage.getRepository().getRefDatabase().findRef(Constants.HEAD);
      if (ref == null || ref.getObjectId() == null) {
        storage
            .commit()
            .setOnly(file.getName())
            .setCommitter(user.getUsername(), user.getUsername())
            .setMessage("Initial Commit of " + documentDescriptor.getName())
            .call();
      }
    } catch (Exception ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public Document checkout() {
    return checkout("main");
  }

  @Override
  public Document checkout(String branchName) {
    try {
      storage.checkout().setName(branchName).call();
      return new FileDocument(owner, file, storage, descriptor);
    } catch (Exception ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  @WillNotClose
  public InputStream read() {
    try {
      return new BufferedInputStream(new FileInputStream(file));
    } catch (IOException ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public void write(@NonNull @WillNotClose InputStream source) {
    try (val outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
      source.transferTo(outputStream);
      outputStream.flush();
    } catch (IOException e) {
      throw new WorkspaceException(e);
    }
  }

  @Override
  public Revision commit(String commitMessage) {
    try {
      val rev =
          storage
              .commit()
              .setMessage(commitMessage)
              .setOnly(file.getName())
              .setCommitter(owner.getUsername(), owner.getUsername())
              .call();
      return new Revision(rev.getId().getName());
    } catch (Exception ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public void checkout(Revision revision) {
    try {
      storage
          .checkout()
          .setName("main")
          .addPath(file.getName())
          .setStartPoint(revision.getName())
          .call();
    } catch (GitAPIException e) {
      throw new WorkspaceException(e);
    }
  }

  @Override
  public List<Revision> getRevisions() {
    try {
      val spliterator = storage.log().all().call();
      return StreamSupport.stream(spliterator.spliterator(), false)
          .map(r -> new Revision(r.getId().name()))
          .collect(Collectors.toList());
    } catch (GitAPIException | IOException ex) {
      throw new WorkspaceException(ex);
    }
  }
}
