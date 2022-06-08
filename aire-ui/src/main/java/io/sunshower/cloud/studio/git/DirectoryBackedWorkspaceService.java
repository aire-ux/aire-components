package io.sunshower.cloud.studio.git;

import static io.zephyr.common.io.FilePermissionChecker.Type.DIRECTORY;
import static io.zephyr.common.io.FilePermissionChecker.Type.READ;
import static io.zephyr.common.io.FilePermissionChecker.Type.WRITE;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.cloud.studio.WorkspaceService;
import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import io.zephyr.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.val;

public class DirectoryBackedWorkspaceService implements WorkspaceService {

  static final String SUFFIX = ".workspace";
  private final @NonNull Supplier<File> root;

  private final @NonNull Condensation condensation;

  public DirectoryBackedWorkspaceService(
      @NonNull final Supplier<File> root, @NonNull final Condensation condensation) {
    this.condensation = condensation;
    this.root =
        () -> {
          try {
            return Files.check(root.get(), WRITE, READ, DIRECTORY);
          } catch (AccessDeniedException ex) {
            throw new WorkspaceException(ex);
          }
        };
  }

  public DirectoryBackedWorkspaceService(
      @NonNull final File root, @NonNull final Condensation condensation) {
    this(() -> root, condensation);
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

  @Override
  public Set<WorkspaceDescriptor> getWorkspaces(User user) {
    return createScopedManager(user).getWorkspaces();
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
    return Files.check(directory, DIRECTORY, READ, WRITE);
  }

  private File getDirectoryFor(Identifier userId) {
    return new File(root.get(), userId.toString() + SUFFIX);
  }
}
