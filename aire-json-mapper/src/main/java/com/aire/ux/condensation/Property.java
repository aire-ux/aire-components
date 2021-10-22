package com.aire.ux.condensation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

public interface Property<T extends AccessibleObject> {

  enum Mode {
    Read,
    Write,
    Normalized
  }

  /**
   * if a property has a converter, apply that converter to convert the property to the desired type
   *
   * @param value
   * @param <T> the incoming type
   * @param <U> the result type
   * @return the converted value
   */
  <T, U> T convert(U value);

  /**
   * @param <U> the type that this property belongs to
   * @return the host of this property
   */
  <U> Class<U> getHost();

  T getMember();

  /**
   * @param <U> the type-parameter
   * @return the type of this property for instance, if this is a field such as {@code private
   *     String myName; } then this returns <code>java.lang.String</code>
   */
  <U> Class<U> getType();

  <T> Type getGenericType();

  /** @return true if the underlying type, returned by <code>getType()</code>is an array */
  boolean isArray();

  /** @return true if the underlying type, returned by <code>getType()</code> is a collection */
  boolean isCollection();

  /**
   * @param <U> the component-type. If this is an array or a collection, this returns the type of
   *     the contents, otherwise it returns <code>getType()</code>
   * @return the type of the contents of this property if it is an array or collection
   */
  <U> Class<U> getComponentType();

  /**
   * @return the physical name of the member if this is a field such as {@code private String
   *     myName; } then this returns <code>myName</code>
   */
  String getMemberReadName();

  String getMemberWriteName();

  /**
   * @return the <code>normalized</code> name of this member. For instance, if you have {@code class
   *     Sample { int myField; int getMyField() { <p>} <p>void setMyField(int myField) {
   *     this.myField = myField; } } } then the <code>
   *     memberReadName()</code> is <code>getMyField()</code> and the <code>memberWriteName()</code>
   *     is <code>setMyField()</code> and the <code>memberNormalizedName</code> is <code>myField
   *     </code>.
   *     <p>Note that the memberNormalizedName is not required to correspond to any field on the
   *     host type
   */
  String getMemberNormalizedName();

  /**
   * the name of the document-element that this should be read from
   *
   * @return the read-alias of this property
   */
  String getReadAlias();

  /** @return the write-alias of this property */
  String getWriteAlias();

  /**
   * @param host the object to set this value on
   * @param value the value to set
   * @param <T> the type of the value
   * @param <U> the type of the host
   */
  <T, U> void set(U host, T value);

  /**
   * @param host the object to retrieve this value from
   * @param <T> the type of the value
   * @param <U> the type of the host
   * @return the value
   */
  <T, U> T get(U host);
}
