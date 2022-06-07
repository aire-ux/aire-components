package io.sunshower.cloud.studio.git;

import static io.zephyr.common.io.FilePermissionChecker.Type.FILE;
import static io.zephyr.common.io.FilePermissionChecker.Type.READ;
import static io.zephyr.common.io.FilePermissionChecker.Type.WRITE;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import io.zephyr.common.io.FilePermissionChecker.Type;
import io.zephyr.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import lombok.NonNull;
import lombok.val;

public class DirectoryBackedWorkspaceService implements WorkspaceService {

  static final String SUFFIX = ".workspace";
  private final @NonNull File root;

  private final @NonNull Condensation condensation;

  public DirectoryBackedWorkspaceService(@NonNull final File root,
      @NonNull final Condensation condensation) throws AccessDeniedException {
    this.condensation = condensation;
    this.root = Files.check(root, Type.WRITE, Type.READ, Type.DIRECTORY);
  }

  @Override
  public WorkspaceManager createScopedManager(User user) {
    try {
      val directory = getOrCreateWorkspaceManagerDirectory(user);
      return new RevisionAwareWorkspaceManager(user, directory, condensation);
    } catch (IOException ex) {
      throw new WorkspaceException(ex);
    }
  }


  private File getOrCreateWorkspaceManagerDirectory(User user) throws IOException {
    val userId = user.getId();
    if (userId == null) {
      throw new IllegalArgumentException("Error: user was not persisted correctly (no user ID)");
    }
    val directory = getDirectoryFor(userId);
    val path = directory.toPath();
    if (!java.nio.file.Files.exists(path)) {
      java.nio.file.Files.createDirectory(path);
    }
    return Files.check(directory, Type.DIRECTORY, Type.READ, Type.WRITE);
  }

  private File getDirectoryFor(Identifier userId) {
    return new File(root, userId.toString() + SUFFIX);
  }
}
