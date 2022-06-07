package io.sunshower.cloud.studio.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import io.sunshower.test.common.Tests;
import java.io.File;
import java.nio.file.AccessDeniedException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkspaceServiceTest {


  static final Condensation condensation;

  static {
    condensation = Condensation.create("json");
  }

  private User user;
  private DirectoryBackedWorkspaceService workspaceService;

  final Sequence<Identifier> sequence = Identifiers.newSequence(true);

  @BeforeEach
  void setUp() throws AccessDeniedException {
    user = new User();
    user.setId(sequence.next());
    user.setUsername("kiersten");
    workspaceService = new DirectoryBackedWorkspaceService(Tests.createTemp(), condensation);
  }


  @Test
  void ensureApiForWorkspaceServiceMakesSense() {
    val manager = workspaceService.createScopedManager(user);
    assertEquals(user, manager.getOwner());
  }

  @Test
  void ensureWorkspaceManagerCanCreateWorkspaceDirectory() {
    val manager = workspaceService.createScopedManager(user);
    val workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = (DirectoryBackedWorkspace) manager.createWorkspace(workspaceDescriptor);
    assertTrue(workspace.getRoot().exists());
    assertTrue(new File(workspace.getRoot(), ".git").exists());
  }

}
