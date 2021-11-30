package com.aire.ux.condensation.json;

import com.aire.ux.condensation.DocumentWriter;
import com.aire.ux.condensation.Property;
import com.aire.ux.condensation.TypeBinder;
import com.aire.ux.condensation.TypeDescriptor;
import com.aire.ux.condensation.TypeInstantiator;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import lombok.NonNull;
import lombok.val;

public class JsonWriter implements DocumentWriter {

  private final TypeBinder binder;
  private final TypeInstantiator instantiator;

  public JsonWriter(TypeBinder typeBinder, TypeInstantiator instantiator) {
    this.binder = typeBinder;
    this.instantiator = instantiator;
  }

  @Override
  public <T> void write(@NonNull Class<T> type, @NonNull T value,
      @NonNull OutputStream outputStream) throws IOException {

    if (type.isArray()) {
      writePrologue(type, outputStream);
      writeArray(type, value, outputStream);
      writeEpilogue(type, outputStream);
    } else {
      val descriptor = binder.descriptorFor(type);
      writePrologue(type, outputStream);
      writeProperties(descriptor, type, outputStream, value);
      writeEpilogue(type, outputStream);
    }


  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private <T> void writeArray(Class<T> type, T value, OutputStream outputStream)
      throws IOException {
    if (Property.isPrimitive(type.getComponentType())) {

    } else {
      val os = (Object[]) value;
      for (int i = 0; i < os.length; i++) {
        val o = os[i];
        if (o != null) {
          write((Class) o.getClass(), o, outputStream);
          if (i < os.length - 1) {
            outputStream.write(',');
          }
        }

      }
//      for (val o : os) {
//      }
    }

  }


  private <T> void writeProperties(TypeDescriptor<T> descriptor, Class<T> type,
      OutputStream outputStream, T value) throws IOException {
    val iterator = descriptor.getProperties().iterator();
    while (iterator.hasNext()) {
      val property = iterator.next();
      if (property.isPrimitive()) {
        writePrimitive(property, value, outputStream);
      }
      if (String.class.equals(property.getType())) {
        writeString(property, value, outputStream);
      } else {
        val v = property.get(value);
        writePropertyPrelude(property, outputStream);
        if (v != null) {
          write(property.getType(), property.get(value), outputStream);
        } else {
          write(outputStream, "null");
        }
      }
      if (iterator.hasNext()) {
        outputStream.write(',');
      }
    }
  }

  private <T> void writeString(Property<?> property, T value, OutputStream outputStream)
      throws IOException {

    writePropertyPrelude(property, outputStream);
    val v = property.get(value);
    if (v != null) {
      outputStream.write('"');
      outputStream.write(((String) v).getBytes(StandardCharsets.UTF_8));
      outputStream.write('"');
    } else {
      write(outputStream, "null");
    }
  }

  private <T> void writePrimitive(Property<?> property, T value, OutputStream outputStream)
      throws IOException {
    writePropertyPrelude(property, outputStream);
    write(outputStream, String.valueOf(property.get(value)));
  }

  void writePropertyPrelude(Property<?> property, OutputStream outputStream) throws IOException {
    outputStream.write('"');
    write(outputStream, property.getWriteAlias());
    outputStream.write('"');
    outputStream.write(':');
  }

  private void write(OutputStream outputStream, String writeAlias) throws IOException {
    outputStream.write(writeAlias.getBytes(StandardCharsets.UTF_8));
  }

  private <T> void writePrologue(Class<T> type, OutputStream outputStream) throws IOException {
    if (isCollection(type)) {
      outputStream.write('[');
    } else {
      outputStream.write('{');
    }
  }

  private <T> void writeEpilogue(Class<T> type, OutputStream outputStream) throws IOException {

    if (isCollection(type)) {
      outputStream.write(']');
    } else {
      outputStream.write('}');
    }

  }

  private <T> boolean isCollection(Class<T> type) {
    return type.isArray() || Collection.class.isAssignableFrom(type);
  }
}
