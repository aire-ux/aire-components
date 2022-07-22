package io.sunshower.cloud.studio;

import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;

public interface WorkspaceManager {

  User getOwner();

  default Optional<Workspace> getWorkspace(@NonNull Identifier id) {
    return getWorkspaceDescriptor(id).flatMap(this::getWorkspace);
  }

  Optional<Workspace> getWorkspace(@NonNull WorkspaceDescriptor descriptor);

  Optional<WorkspaceDescriptor> getWorkspaceDescriptor(@NonNull Identifier id);

  Set<WorkspaceDescriptor> getWorkspaces();

  Workspace createWorkspace(WorkspaceDescriptor workspaceDescriptor);

  void delete(WorkspaceDescriptor descriptor);

  void flush();
}
