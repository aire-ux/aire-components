package com.aire.ux.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.aire.ux.theme.ResourceDefinition.Source;
import com.vaadin.flow.internal.JsonSerializer;
import elemental.json.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ResourceDefinitionTest {

  @AllArgsConstructor
  @NoArgsConstructor
  public static class A {
    @Getter @Setter private Source source;
  }

  @ParameterizedTest
  @EnumSource(Source.class)
  void ensureSourceIsConvertedToValuesProperly(Source source) {
    val value = JsonSerializer.toJson(new A(source)).toJson();
    val parsed = Json.parse(value);
    assertEquals(source, Source.valueOf(parsed.get("source").asString()));
  }
}
