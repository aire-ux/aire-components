package io.sunshower.zephyr.ui.rmi;

import lombok.val;

public class Parameters {

  @SuppressWarnings("unchecked")
  public static String constructExpression(String methodName,
      Class<?>... values) {

    val result = new StringBuilder("this.").append(methodName).append("(");
    for (int i = 0; i < values.length; i++) {
      result.append("$").append(i);
      if (i < values.length - 1) {
        result.append(",");
      }
    }
    return result.append(")").toString();
  }

}
