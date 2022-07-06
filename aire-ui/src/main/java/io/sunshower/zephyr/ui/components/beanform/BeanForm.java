package io.sunshower.zephyr.ui.components.beanform;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.val;

/**
 * a bean form binds a type to a form. When the form is populated, the bean's bound properties will
 * be, too
 *
 * @param <T>
 */
public class BeanForm<T> extends FormLayout {

  private final T value;
  private final Class<T> type;

  public BeanForm(@NonNull final Class<T> type, @NonNull final T value) {
    this.type = type;
    this.value = value;
    scan(type);
  }

  public T getValue() {
    return value;
  }

  public List<ResponsiveStep> getResponsiveSteps() {
    val stepsJsonArray = (JsonArray) getElement().getPropertyRaw("responsiveSteps");
    if (stepsJsonArray == null) {
      return Collections.emptyList();
    }
    List<ResponsiveStep> steps = new ArrayList<>();
    for (int i = 0; i < stepsJsonArray.length(); i++) {
      steps.add(readJson(stepsJsonArray.get(i)));
    }
    return steps;
  }

  private ResponsiveStep readJson(JsonObject value) {
    val minWidth = value.getString("minWidth");
    val columns = (int) value.getNumber("columns");
    var lp = (JsonString) value.get("labelsPosition");

    LabelsPosition position = null;
    if (lp != null) {
      val lps = lp.getString();
      if ("aside".equals(lps)) {
        position = LabelsPosition.ASIDE;
      } else if ("top".equals(lps)) {
        position = LabelsPosition.TOP;
      }
    }
    return new ResponsiveStep(minWidth, columns, position);
  }

  private void scan(Class<T> type) {
    scanResponsiveSteps(type);
  }

  private void scanResponsiveSteps(Class<T> type) {
    setResponsiveSteps(
        Arrays.stream(type.getAnnotationsByType(ResponsiveBreak.class))
            .map(this::scanResponsiveStep)
            .collect(Collectors.toList()));
  }

  private ResponsiveStep scanResponsiveStep(ResponsiveBreak breakpoint) {
    val width = breakpoint.width();
    val columns = breakpoint.columns();
    val position =
        switch (breakpoint.position()) {
          case Top -> LabelsPosition.TOP;
          case Aside -> LabelsPosition.ASIDE;
        };
    return new ResponsiveStep(width, columns);
  }


}
