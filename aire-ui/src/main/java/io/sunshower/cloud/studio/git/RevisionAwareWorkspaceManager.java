package io.sunshower.cloud.studio.git;

import static io.zephyr.common.io.FilePermissionChecker.Type.DIRECTORY;
import static io.zephyr.common.io.FilePermissionChecker.Type.FILE;
import static io.zephyr.common.io.FilePermissionChecker.Type.READ;
import static io.zephyr.common.io.FilePermissionChecker.Type.WRITE;
import static org.springframework.util.FileSystemUtils.deleteRecursively;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.Workspace;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.cloud.studio.WorkspaceException;
import io.sunshower.cloud.studio.WorkspaceManager;
import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import io.zephyr.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import lombok.val;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * workspaces have the following directory-structure:
 *
 * <p>1. Root: the root is the directory that the workspace service has been instantiated with 2.
 * Root/userId.workspace: The ID of the workspace directory 3.
 * Root/userId.workspace/workspace.definition: the workspace definition file (JSON) 4.
 * Root/userId.workspace/workspace: the workspace directory (git is initialized here)
 */
public class RevisionAwareWorkspaceManager implements WorkspaceManager {

  public static final String WORKSPACE_DEFINITION = "workspace.definition";
  /** the root directory of this workspace manager */
  private final File root;

  /** the owner of this workspace manager */
  private final User owner;

  private final Condensation condensation;

  private WorkspaceSet workspaces;

  /**
   * @param user the owner for this workspace manager
   * @param root the root directory
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
  public Optional<Workspace> getWorkspace(@NonNull WorkspaceDescriptor descriptor) {
    if (getWorkspaces().contains(descriptor)) {
      try {
        return Optional.of(populateWorkspace(descriptor));
      } catch (Exception ex) {
        throw new WorkspaceException(ex);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<WorkspaceDescriptor> getWorkspaceDescriptor(@NonNull Identifier id) {
    return Optional.ofNullable(workspaces.get(id));
  }

  @Override
  public Set<WorkspaceDescriptor> getWorkspaces() {
    return Set.copyOf(workspaces.getWorkspaces().values());
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

  @Override
  public void delete(WorkspaceDescriptor descriptor) {
    try {
      assert workspaces != null;
      workspaces.remove(descriptor);
      val definitionFile = getOrCreateWorkspaceDefinitionFile(root);
      val workspaceRoot = getWorkspaceRoot(descriptor.getId());
      deleteRecursively(workspaceRoot);
      writeDescriptor(condensation, definitionFile);
    } catch (IOException ex) {
      throw new WorkspaceException(ex);
    }
  }

  @Override
  public void flush() {
    try {
      val definitionFile = getOrCreateWorkspaceDefinitionFile(root);
      writeDescriptor(condensation, definitionFile);
    } catch (IOException ex) {
      throw new WorkspaceException(ex);
    }
  }

  void removeWorkspaceById(Identifier id) {
    val result = workspaces.get(id);
    if (result != null) {
      delete(result);
    }
  }

  private Workspace populateWorkspace(WorkspaceDescriptor result)
      throws IOException, GitAPIException {
    createOrPopulateWorkspaceDescriptor(result);
    val root = getWorkspaceRoot(result.getId());
    val workspaceDirectory = new File(root, "workspace");
    final Git git;
    if (!workspaceDirectory.exists()) {
      createAndPopulate(workspaceDirectory);
      git = Git.init().setDirectory(workspaceDirectory).setInitialBranch("main").call();
    } else {
      git = Git.open(workspaceDirectory);
    }
    return new DirectoryBackedWorkspace(git, workspaceDirectory, this, result.getId());
  }

  private File getWorkspaceRoot(Identifier id) throws IOException {
    val directory = new File(root, id.toString());
    if (!directory.exists()) {
      java.nio.file.Files.createDirectory(directory.toPath());
    }
    return Files.check(directory, DIRECTORY, READ, WRITE);
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
    }
    return readDescriptor();
  }

  private void writeDescriptor(Condensation condensation, File definitionFile) throws IOException {
    val definition = condensation.write(WorkspaceSet.class, workspaces);
    java.nio.file.Files.writeString(
        definitionFile.toPath(), definition, StandardOpenOption.CREATE, StandardOpenOption.SYNC);
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

  public void registerDescriptor(Identifier id, DocumentDescriptor descriptor) {
    val ws = workspaces.get(id);
    assert ws != null;
    if (ws.getDocument(descriptor.getId()) == null) {
      ws.addDocument(descriptor);
      flush();
    }
  }
}
