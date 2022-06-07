package io.sunshower.cloud.studio;

import lombok.Getter;

public class Revision {

  @Getter
  private final String name;

  public Revision(String name) {
    this.name = name;
  }
}
