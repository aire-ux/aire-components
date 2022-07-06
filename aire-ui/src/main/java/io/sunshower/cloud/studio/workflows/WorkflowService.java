package io.sunshower.cloud.studio.workflows;

import java.util.List;

public interface WorkflowService {

  List<WorkflowDescriptor> getWorkflows();

  void addWorkflow(WorkflowDescriptor descriptor);

  void removeWorkflow(WorkflowDescriptor descriptor);
}
