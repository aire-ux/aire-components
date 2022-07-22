package io.sunshower.cloud.studio.workflows;

import com.aire.ux.Registration;
import com.aire.ux.zephyr.ZephyrService;
import java.util.List;

@ZephyrService(type = WorkflowService.class)
public interface WorkflowService {

  List<WorkflowDescriptor> getWorkflows();

  default Registration addWorkflow(WorkflowDescriptor descriptor) {
    return () -> removeWorkflow(descriptor);
  }

  void removeWorkflow(WorkflowDescriptor descriptor);
}
