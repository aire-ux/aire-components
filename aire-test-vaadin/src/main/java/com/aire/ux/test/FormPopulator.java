package com.aire.ux.test;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import java.util.NoSuchElementException;
import lombok.val;

public class FormPopulator {

  private final TestContext context;

  public FormPopulator(TestContext context) {
    this.context = context;
  }

  public <E extends ValueChangeEvent<V>, V> FieldPopulationRequest<E, V> field(String selector) {
    val field =
        context
            .selectFirst(selector, HasValue.class)
            .orElseThrow(() -> new NoSuchElementException("No element at '" + selector + "'"));
    return new FieldPopulationRequest<>(field);
  }

  public final class FieldPopulationRequest<E extends ValueChangeEvent<V>, V> {
    private final HasValue<E, V> field;

    public FieldPopulationRequest(HasValue<E, V> field) {
      this.field = field;
    }

    public FormPopulator value(V value) {
      field.setValue(value);
      return FormPopulator.this;
    }
  }
}
