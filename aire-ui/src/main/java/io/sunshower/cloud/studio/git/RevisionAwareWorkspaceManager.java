package io.sunshower.cloud.studio.git;

import static io.zephyr.common.io.FilePermissionChecker.Type.DIRECTORY;
import static io.zephyr.common.io.FilePermissionChecker.Type.FILE;
import static io.zephyr.common.io.FilePermissionChecker.Type.READ;
import static io.zephyr.common.io.FilePermissionChecker.Type.WRITE;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.model.api.User;
import io.zephyr.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import lombok.NonNull;
import lombok.val;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * workspaces have the following directory-structure:
 * <p>
 * 1. Root: the root is the directory that the workspace service has been instantiated with 2.
 * Root/userId.workspace: The ID of the workspace directory 3. Root/userId.workspace/workspace.definition:
 * the workspace definition file (JSON) 4. Root/userId.workspace/workspace: the workspace directory
 * (git is initialized here)
 */
public class RevisionAwareWorkspaceManager implements WorkspaceManager {


  public static final String WORKSPACE_DEFINITION = "workspace.definition";
  /**
   * the root directory of this workspace manager
   */
  private final File root;

  /**
   * the owner of this workspace manager
   */
  private final User owner;
  private final Condensation condensation;


  private WorkspaceSet workspaces;

  /**
   * @param user         the owner for this workspace manager
   * @param root         the root directory
   * @param condensation
   */

  public RevisionAwareWorkspaceManager(User user, File root, Condensation condensation)
      throws IOException {
    this.root = root;
    this.owner = user;
    this.condensation = condensation;
    this.workspaces = readDescriptors(root, condensation);
  }


  @Override
  public User getOwner() {
    return owner;
  }

  @Override
  public Workspace createWorkspace(@NonNull WorkspaceDescriptor workspaceDescriptor) {
    try {
      assert workspaces != null;
      val result = createOrPopulateWorkspaceDescriptor(workspaceDescriptor);
      return populateWorkspace(result);
    } catch (IOException | GitAPIException ex) {
      throw new WorkspaceException(ex);
    }
  }

  private Workspace populateWorkspace(WorkspaceDescriptor result)
      throws IOException, GitAPIException {
    createOrPopulateWorkspaceDescriptor(result);
    val workspaceDirectory = new File(root, "workspace");
    final Git git;
    if (!workspaceDirectory.exists()) {
      createAndPopulate(workspaceDirectory);
      git = Git.init().setDirectory(workspaceDirectory).call();
    } else {
      git = Git.open(workspaceDirectory);
    }
    return new DirectoryBackedWorkspace(git, workspaceDirectory);
  }

  private File createAndPopulate(File workspaceDirectory) throws IOException {
    val path = workspaceDirectory.toPath();
    java.nio.file.Files.createDirectory(path);
    return Files.check(workspaceDirectory, DIRECTORY, READ, WRITE);
  }

  private WorkspaceDescriptor createOrPopulateWorkspaceDescriptor(
      @NonNull WorkspaceDescriptor workspaceDescriptor) throws IOException {
    workspaces.add(workspaceDescriptor);
    writeDescriptor(condensation, new File(root, WORKSPACE_DEFINITION));
    return readDescriptors(root, condensation).get(workspaceDescriptor.getId());
  }


  private WorkspaceSet readDescriptors(File root, Condensation condensation) throws IOException {
    val definitionFile = getOrCreateWorkspaceDefinitionFile(root);
    if (definitionFile.length() == 0) {
      workspaces = new WorkspaceSet();
      workspaces.setOwner(owner);
      writeDescriptor(condensation, definitionFile);
      return workspaces;
    } else {
      return readDescriptor();
    }
  }

  private void writeDescriptor(Condensation condensation, File definitionFile) throws IOException {
    val definition = condensation.write(WorkspaceSet.class, workspaces);
    java.nio.file.Files.writeString(definitionFile.toPath(), definition,
        StandardOpenOption.CREATE, StandardOpenOption.SYNC);
  }

  private WorkspaceSet readDescriptor() throws IOException {
    val definitionFile = getOrCreateWorkspaceDefinitionFile(root);
    try (val inputStream = new FileInputStream(definitionFile)) {
      return workspaces = condensation.read(WorkspaceSet.class, inputStream);
    }
  }

  private File getOrCreateWorkspaceDefinitionFile(File directory) throws IOException {
    val ws = new File(directory, WORKSPACE_DEFINITION);
    if (ws.exists() && ws.isFile()) {
      return ws;
    }
    if (!ws.exists()) {
      java.nio.file.Files.createFile(ws.toPath());
    }
    if (!ws.isFile()) {
      throw new IOException("Error: File '%s' exists, but is not a file".formatted(ws));
    }
    return Files.check(ws, FILE, READ, WRITE);
  }
}
