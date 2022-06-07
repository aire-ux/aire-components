package io.sunshower.cloud.studio;

public class WorkspaceException extends RuntimeException {

  public WorkspaceException(String message, final Exception cause) {
    super(message, cause);
  }

  public WorkspaceException(final Exception cause) {
    super(cause);
  }
}
