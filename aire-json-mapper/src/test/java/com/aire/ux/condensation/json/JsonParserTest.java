package com.aire.ux.condensation.json;

import static org.junit.jupiter.api.Assertions.*;

import lombok.val;
import org.junit.jupiter.api.Test;

class JsonParserTest {


  @Test
  void ensureParsingStringsWorks() {
    val obj = "{ \"hello\": \"world\" , \"howare\"  : \"you?\" }";
    val ast = new JsonParser().parse(obj);
    System.out.println(ast);
  }
}