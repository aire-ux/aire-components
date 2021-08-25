package com.aire.ux.condensation.json;

import static com.aire.ux.condensation.json.JsonParserTest.read;
import static org.junit.jupiter.api.Assertions.*;

import com.aire.ux.condensation.Condensation;
import lombok.val;
import org.junit.jupiter.api.Test;

class JsonParserFactoryTest {


  @Test
  void ensureParsingDocumentWorks() {
    val document = Condensation.parse("json", read("test.json"));
    val value = document.select(".world > .five");
    assertEquals(value, 5);
  }

  @Test
  void ensureSiblingSelectorWorks() {
    val document = Condensation.parse("json", read("test.json"));
    val value = document.selectAll(".world > .five ~ number");
    System.out.println(value);

  }

}