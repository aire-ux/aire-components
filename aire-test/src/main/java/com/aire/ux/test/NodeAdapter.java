package com.aire.ux.test;

import java.util.List;

public interface NodeAdapter<T> {

  String ID = "id";
  String CLASS = "class";

  List<T> getChildren(T current);

  String getAttribute(T current, String key);

  default String getId(T current) {
    return getAttribute(current, ID);
  }

  default String getClass(T current) {
    return getAttribute(current, ID);
  }


}
