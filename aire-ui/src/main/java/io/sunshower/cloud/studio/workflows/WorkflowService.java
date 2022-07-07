package io.sunshower.cloud.studio.workflows;

import com.aire.ux.Registration;
import java.util.List;

public interface WorkflowService {

  List<WorkflowDescriptor> getWorkflows();

  default Registration addWorkflow(WorkflowDescriptor descriptor) {
    return () -> removeWorkflow(descriptor);
  }

  void removeWorkflow(WorkflowDescriptor descriptor);
}
