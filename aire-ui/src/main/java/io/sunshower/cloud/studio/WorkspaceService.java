package io.sunshower.cloud.studio;

import io.sunshower.model.api.User;

public interface WorkspaceService {

  WorkspaceManager createScopedManager(User user);
}
