package io.sunshower.cloud.studio.git;

import io.sunshower.cloud.studio.Workspace;
import java.io.File;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.jgit.api.Git;

public class DirectoryBackedWorkspace implements Workspace {

  @Getter
  private final File root;
  private final Git storage;

  public DirectoryBackedWorkspace(@NonNull Git storage, @NonNull File root) {
    this.root = root;
    this.storage = storage;
  }
}
