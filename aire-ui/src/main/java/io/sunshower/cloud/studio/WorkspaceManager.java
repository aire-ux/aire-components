package io.sunshower.cloud.studio;

import io.sunshower.model.api.User;

public interface WorkspaceManager {

  User getOwner();

  Workspace createWorkspace(WorkspaceDescriptor workspaceDescriptor);
}
