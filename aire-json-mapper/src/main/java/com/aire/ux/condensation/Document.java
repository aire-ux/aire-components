package com.aire.ux.condensation;

import com.aire.ux.condensation.json.Value;
import com.aire.ux.condensation.json.Values.ObjectValue;
import java.util.Collection;

public interface Document {

  ObjectValue getRoot();

  <U> U get(String key);

  <U, T extends Value<U>> T getValue(String key);

  <T> T select(String selector);

  Collection<?> selectAll(String selector);

  <T> T read(Class<T> type, PropertyMappingStrategy strategy);
}