package io.sunshower.cloud.studio.workflows;

import com.aire.ux.Registration;
import com.aire.ux.zephyr.ZephyrService;
import java.util.ArrayList;
import java.util.List;

@ZephyrService(type = WorkflowService.class)
public class ModuleDrivenWorkflowService implements WorkflowService {

  private List<WorkflowDescriptor> descriptors;

  public ModuleDrivenWorkflowService() {
    descriptors = new ArrayList<>();
  }

  @Override
  public Registration addWorkflow(WorkflowDescriptor descriptor) {
    descriptors.add(descriptor);
    return WorkflowService.super.addWorkflow(descriptor);
  }

  @Override
  public List<WorkflowDescriptor> getWorkflows() {
    return descriptors;
  }

  @Override
  public void removeWorkflow(WorkflowDescriptor descriptor) {
    descriptors.remove(descriptor);
  }
}
