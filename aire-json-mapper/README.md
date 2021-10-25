# Object Mapper

The Aire object mapper is intended to be a lightweight and (reasonably) fast,
external-dependency-free interchange format (JSON, XML, etc.) binder.

## Why another mapper?
Condensation seeks to provide first-class support for dynamic polymorphism.  For instance, in JAXB,
one must enumerate all of the subtypes that an element-type may have, or rely on implementation-specific details.
Libraries like Jackson have better support, but can be heavy depending on the features required.  Condensation aims to
be fast, simple, and lightweight


## Usage (JSON)

### Simple Scenario

The annotation `@RootElement` denotes a type that can be bound to a document element. For instance:

```json
{
  "firstName": "Josiah",
  "lastName": "Haswell"
}

```

could correspond to the class:

```java

@RootElement
public class Person {

  @Attribute
  private String firstName;

  @Attribute
  private String lastName;
}
```

You can then bind the JSON document to an instance of the object as follows:

```java

class Example {

  public Example() {
    String document = "{"
                      + "\"firstName\":\"Josiah\","
                      + "\"lastName\":\"Haswell\""
                      + "}";
    Condensation condensation = Condensation.create("json");
    Person person = condensation.read(Person.class, document);
  }
}
```

Condensation supports arbitrarily-nested objects, Arrays, Lists, Maps, etc.

### Arrays

Condensation natively supports arrays of objects, primitive arrays, etc.

#### Primitive Arrays

The default numeric type for JSON/JavaScript is 8-byte IEEE 754 floating-point. Any primitive
numeric type (or their wrappers) may be used and conversions are automatically applied

```java

class ExampleInts {

  public Example() {
    Condensation condensation = Condensation.create("json");
    int[] values = condensation.read(int[].class, "[1,2,3,4]");
  }
}

/**
 *
 */
class ExampleDoubles {

  public Example() {
    Condensation condensation = Condensation.create("json");
    double[] values = condensation.read(double[].class, "[1,2,3,4]");
  }
}


class ExampleStrings {

  public Example() {
    Condensation condensation = Condensation.create("json");
    String[] values = condensation.read(String[].class, "[\"1\",\"2\",\"3\",\"4\"]");
  }
}

```

### Arrays of Objects

Condensation also supports arrays of objects.  The `Person` type mapped above may be read as follows:
```java

class Example {
  public Example() {
    Condensation condensation = Condensation.create("json");
    String value = "[\n"
                   + "  {\n"
                   + "    \"firstName\": \"Josiah\",\n"
                   + "    \"lastName\": \"Haswell\"\n"
                   + "  },\n"
                   + "\n"
                   + "  {\n"
                   + "    \"firstName\": \"Bob\",\n"
                   + "    \"lastName\": \"Porgnorgler\"\n"
                   + "  }\n"
                   + "]";
    Person[] values = condensation.read(Person[].class, value);
  }
}
```

### Maps

Condensation supports maps whose values may be any mapped type or primitive,
and whose keys may be mapped to strings.  

```java
    class StringToIntegerConverter implements Function<String, Integer> {

      @Override
      public Integer apply(String s) {
        return Integer.parseInt(s);
      }
    }
    @RootElement
    class KV {

      @Element
      @Convert(key = StringToIntegerConverter.class)
      Map<Integer, Integer> elements;

    }
    val value = "{" + "\"elements\": {" + "\"1\": 1," + "\"2\": 3}" + "} ";
    KV kv = condensation.read(KV.class, value);
```

Converters may be applied for both the key and the value of a map:

```java

class IntegerToStringConverter implements Function<Integer, String> {

  @Override
  public String apply(Integer s) {
    return s.toString();
  }
}
class StringToIntegerConverter implements Function<String, Integer> {

  @Override
  public Integer apply(String s) {
    return Integer.parseInt(s);
  }
}
@RootElement
class KV {

  @Element
  @Convert(
      key = StringToIntegerConverter.class, 
      value = IntegerToStringConverter.class
  )
  Map<Integer, Integer> elements;

}
val value = "{" + "\"elements\": {" + "1: \"1\"," + "2: \"3\"}" + "} ";
KV kv = condensation.read(KV.class, value);
```


