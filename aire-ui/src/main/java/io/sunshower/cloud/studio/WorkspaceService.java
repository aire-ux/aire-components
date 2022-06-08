package io.sunshower.cloud.studio;

import io.sunshower.model.api.User;
import java.util.Set;

public interface WorkspaceService {

  WorkspaceManager createScopedManager(User user);

  Set<WorkspaceDescriptor> getWorkspaces(User user);
}
