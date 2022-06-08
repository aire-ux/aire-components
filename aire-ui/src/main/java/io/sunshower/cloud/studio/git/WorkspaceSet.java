package io.sunshower.cloud.studio.git;

import io.sunshower.arcus.condensation.Convert;
import io.sunshower.arcus.condensation.Element;
import io.sunshower.arcus.condensation.RootElement;
import io.sunshower.cloud.studio.WorkspaceDescriptor;
import io.sunshower.model.api.IdentifierConverter;
import io.sunshower.model.api.User;
import io.sunshower.persistence.id.Identifier;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@RootElement
public final class WorkspaceSet {

  @Setter @Getter @Element private User owner;

  @Getter
  @Setter
  @Element
  @Convert(key = IdentifierConverter.class)
  private Map<Identifier, WorkspaceDescriptor> workspaces;

  public WorkspaceSet(User user) {
    setOwner(user);
    workspaces = new HashMap<>();
  }

  public WorkspaceSet() {
    workspaces = new HashMap<>();
  }

  public void add(WorkspaceDescriptor workspaceDescriptor) {
    workspaces.put(workspaceDescriptor.getId(), workspaceDescriptor);
  }

  public void remove(WorkspaceDescriptor descriptor) {
    workspaces.remove(descriptor.getId());
  }

  @NonNull
  public WorkspaceDescriptor get(Identifier id) {
    return workspaces.get(id);
  }
}
