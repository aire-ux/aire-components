package io.sunshower.zephyr;

import io.zephyr.kernel.core.KernelPackageReexportConstraintSetProvider;
import java.util.Set;
import lombok.NonNull;

public class AireKernelPackageReexportConstraintSetProvider
    implements KernelPackageReexportConstraintSetProvider {

  @Override
  public Mode getMode() {
    return Mode.Include;
  }

  @Override
  public Set<String> getPackages() {
    return Set.of("com.vaadin.*", "com.aire.ux.*", "io.sunshower.zephyr.aire.*");
  }

  @Override
  public int compareTo(@NonNull KernelPackageReexportConstraintSetProvider other) {
    return Integer.compare(getPrecedence(), other.getPrecedence());
  }
}
