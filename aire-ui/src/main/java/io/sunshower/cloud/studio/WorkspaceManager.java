package io.sunshower.cloud.studio;

import io.sunshower.model.api.User;
import java.util.Set;

public interface WorkspaceManager {

  User getOwner();

  Set<WorkspaceDescriptor> getWorkspaces();

  Workspace createWorkspace(WorkspaceDescriptor workspaceDescriptor);

  void delete(WorkspaceDescriptor descriptor);
}
