package io.sunshower.cloud.studio.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.sunshower.arcus.condensation.Condensation;
import io.sunshower.cloud.studio.DocumentDescriptor;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import io.sunshower.persistence.id.Identifiers;
import io.sunshower.persistence.id.Sequence;
import io.sunshower.test.common.Tests;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkspaceServiceTest {

  static final Condensation condensation;

  static {
    condensation = Condensation.create("json");
  }

  final Sequence<Identifier> sequence = Identifiers.newSequence(true);
  private User user;
  private DirectoryBackedWorkspaceService workspaceService;
  private File directory;

  @BeforeEach
  void setUp() throws AccessDeniedException {
    directory = Tests.createTemp();
    user = new User();
    user.setId(sequence.next());
    user.setUsername("kiersten");
    workspaceService = new DirectoryBackedWorkspaceService(directory, condensation);
  }

  @Test
  void ensureApiForWorkspaceServiceMakesSense() {
    val manager = workspaceService.createScopedManager(user);
    assertEquals(user, manager.getOwner());
  }

  @Test
  void ensureDocumentsAreSavedCorrectly() {
    val ws = new WorkspaceDescriptor();
    ws.setName("hello");

    var mgr = workspaceService.createScopedManager(user);
    var workspace = mgr.createWorkspace(ws);

    var doc = new DocumentDescriptor();
    doc.setName("test");
    workspace.getOrCreate(doc);

    mgr = workspaceService.createScopedManager(user);
    workspace = mgr.getWorkspace(ws.getId()).get();

    assertEquals(1, workspace.getDocuments().size());
  }

  @Test
  void ensureRetrievingCreatedWorkspaceAndDocumentOnItWorks() {
    val descriptor = new WorkspaceDescriptor();
    descriptor.setName("sup");
    val mgr = workspaceService.createScopedManager(user);
    val workspace = mgr.createWorkspace(descriptor);

    val doc = new DocumentDescriptor();
    doc.setName("test");

    workspace.getOrCreate(doc);
    assertEquals(1, workspace.getDocuments().size());
  }

  @Test
  void ensureRetrievingCreatedWorkspaceWorks() {
    val descriptor = new WorkspaceDescriptor();
    descriptor.setName("sup");
    val mgr = workspaceService.createScopedManager(user);
    mgr.createWorkspace(descriptor);
    assertFalse(mgr.getWorkspace(descriptor).isEmpty());
  }

  @Test
  void ensureRetrievingUncreatedWorkspaceWorks() {
    val descriptor = new WorkspaceDescriptor();
    descriptor.setName("sup");
    val mgr = workspaceService.createScopedManager(user);
    assertTrue(mgr.getWorkspace(descriptor).isEmpty());
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

  @Test
  void ensureCreatedWorkspacesAreNotInSameDirectory() {
    val manager = workspaceService.createScopedManager(user);
    var workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = (DirectoryBackedWorkspace) manager.createWorkspace(workspaceDescriptor);

    workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test 2");
    val ws = (DirectoryBackedWorkspace) manager.createWorkspace(workspaceDescriptor);
    assertNotEquals(ws.getRoot(), workspace.getRoot());
  }

  @Test
  void ensureWorkspaceManagerCanDeleteWorkspaceAndCorrespondingFiles() {
    val manager = workspaceService.createScopedManager(user);
    val workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = (DirectoryBackedWorkspace) manager.createWorkspace(workspaceDescriptor);
    assertTrue(workspace.getRoot().exists());
    assertTrue(new File(workspace.getRoot(), ".git").exists());
    workspace.delete();
    assertFalse(workspace.getRoot().exists());
    assertFalse(new File(workspace.getRoot(), ".git").exists());
  }

  @Test
  void ensureDeletingDocumentWorks() {
    val manager = workspaceService.createScopedManager(user);
    val workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = manager.createWorkspace(workspaceDescriptor);

    val doc = new DocumentDescriptor();
    doc.setName("lol");

    val document = (FileDocument) workspace.getOrCreate(doc);
    assertNotNull(workspace.getDocumentDescriptor(doc.getId()));
    workspace.delete(doc);
    assertTrue(workspace.getDocuments().isEmpty());
    assertFalse(document.getRoot().exists());
  }

  @Test
  void ensureCreatingDocumentAllowsDocumentToBeRetrievedFromWorkspace() {
    val manager = workspaceService.createScopedManager(user);
    val workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = manager.createWorkspace(workspaceDescriptor);

    val doc = new DocumentDescriptor();
    doc.setName("lol");

    workspace.getOrCreate(doc);
    assertNotNull(workspace.getDocumentDescriptor(doc.getId()));
  }

  @Test
  @SneakyThrows
  void ensureOpeningWorkspaceServiceResultsInWorkspacesBecomingAvailable() {
    val manager = workspaceService.createScopedManager(user);
    var workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    manager.createWorkspace(workspaceDescriptor);
    workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test 2");
    manager.createWorkspace(workspaceDescriptor);
    val wss = new DirectoryBackedWorkspaceService(directory, condensation);
    assertEquals(2, wss.getWorkspaces(user).size());
  }

  @Test
  @SneakyThrows
  void ensureWorkspaceCanAddDocument() {
    val manager = workspaceService.createScopedManager(user);
    val workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = manager.createWorkspace(workspaceDescriptor);

    val documentDescriptor = new DocumentDescriptor();
    documentDescriptor.setName("My Test Document");

    val document = workspace.getOrCreate(documentDescriptor);
    document.write(new ByteArrayInputStream("hello world".getBytes(StandardCharsets.UTF_8)));

    val outputStream = new ByteArrayOutputStream();
    try (val inputStream = document.read()) {
      inputStream.transferTo(outputStream);
    }
    assertEquals(outputStream.toString(StandardCharsets.UTF_8), "hello world");
  }

  @Test
  @SneakyThrows
  void ensureCommittingDocumentWorks() {
    val manager = workspaceService.createScopedManager(user);
    val workspaceDescriptor = new WorkspaceDescriptor();
    workspaceDescriptor.setName("Test Workspace");
    val workspace = manager.createWorkspace(workspaceDescriptor);

    val documentDescriptor = new DocumentDescriptor();
    documentDescriptor.setName("My Test Document");

    val document = workspace.getOrCreate(documentDescriptor);
    document.write(new ByteArrayInputStream("hello world".getBytes(StandardCharsets.UTF_8)));

    document.commit("Saving progress");
    var outputStream = new ByteArrayOutputStream();
    try (val inputStream = document.read()) {
      inputStream.transferTo(outputStream);
    }
    assertEquals("hello world", outputStream.toString(StandardCharsets.UTF_8));

    document.write(new ByteArrayInputStream("hello wab".getBytes(StandardCharsets.UTF_8)));
    assertEquals(2, document.getRevisions().size());
    val orevision = document.commit("Saving world vs. wab");
    assertEquals(3, document.getRevisions().size());

    outputStream = new ByteArrayOutputStream();
    try (val inputStream = document.read()) {
      inputStream.transferTo(outputStream);
    }
    assertEquals("hello wab", outputStream.toString(StandardCharsets.UTF_8));
    val revision = document.getRevisions().get(1);

    document.checkout(revision);
    outputStream = new ByteArrayOutputStream();
    try (val inputStream = document.read()) {
      inputStream.transferTo(outputStream);
    }
    assertEquals("hello world", outputStream.toString(StandardCharsets.UTF_8));
  }
}
