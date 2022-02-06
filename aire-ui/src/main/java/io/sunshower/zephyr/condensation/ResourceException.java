package io.sunshower.zephyr.condensation;

import java.io.IOException;

public class ResourceException extends RuntimeException {

  public ResourceException(IOException ex) {
    super(ex);
  }
}
