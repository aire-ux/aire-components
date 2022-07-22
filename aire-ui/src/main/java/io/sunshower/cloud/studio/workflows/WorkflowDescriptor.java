package io.sunshower.cloud.studio.workflows;

public record WorkflowDescriptor(
    String name, String description, String moduleIconPath, String initialRoute) {}
