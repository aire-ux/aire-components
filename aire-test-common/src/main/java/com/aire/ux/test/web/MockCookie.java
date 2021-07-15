/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aire.ux.test.web;

import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;


/**
 * Extension of {@code Cookie} with extra attributes, as defined in
 * <a href="https://tools.ietf.org/html/rfc6265">RFC 6265</a>.
 *
 * @author Vedran Pavic
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 5.1
 */
public class MockCookie extends Cookie {

  private static final long serialVersionUID = 4312531139502726325L;


  @Nullable
  private ZonedDateTime expires;

  @Nullable
  private String sameSite;


  /**
   * Construct a new {@link MockCookie} with the supplied name and value.
   *
   * @param name  the name
   * @param value the value
   * @see Cookie#Cookie(String, String)
   */
  public MockCookie(String name, String value) {
    super(name, value);
  }

  /**
   * Factory method that parses the value of the supplied "Set-Cookie" header.
   *
   * @param setCookieHeader the "Set-Cookie" value; never {@code null} or empty
   * @return the created cookie
   */
  public static MockCookie parse(String setCookieHeader) {
    Objects.requireNonNull(setCookieHeader, "Set-Cookie header must not be null");
    String[] cookieParts = setCookieHeader.split("\\s*=\\s*", 2);

    String name = cookieParts[0];
    String[] valueAndAttributes = cookieParts[1].split("\\s*;\\s*", 2);
    String value = valueAndAttributes[0];
    String[] attributes =
        (valueAndAttributes.length > 1 ? valueAndAttributes[1].split("\\s*;\\s*") : new String[0]);

    MockCookie cookie = new MockCookie(name, value);
    for (String attribute : attributes) {
      if (attribute.toLowerCase().startsWith("domain")) {
        cookie.setDomain(extractAttributeValue(attribute, setCookieHeader));
      } else if (attribute.toLowerCase(Locale.ROOT).startsWith("max-age")) {
        cookie.setMaxAge(Integer.parseInt(extractAttributeValue(attribute, setCookieHeader)));
      } else if (attribute.toLowerCase(Locale.ROOT).startsWith("expires")) {
        try {
          cookie.setExpires(ZonedDateTime.parse(extractAttributeValue(attribute, setCookieHeader),
              DateTimeFormatter.RFC_1123_DATE_TIME));
        } catch (DateTimeException ex) {
          // ignore invalid date formats
        }
      } else if (attribute.toLowerCase(Locale.ROOT).startsWith("path")) {
        cookie.setPath(extractAttributeValue(attribute, setCookieHeader));
      } else if (attribute.toLowerCase(Locale.ROOT).startsWith("secure")) {
        cookie.setSecure(true);
      } else if (attribute.toLowerCase(Locale.ROOT).startsWith("httponly")) {
        cookie.setHttpOnly(true);
      } else if (attribute.startsWith("samesite")) {
        cookie.setSameSite(extractAttributeValue(attribute, setCookieHeader));
      }
    }
    return cookie;
  }

  private static String extractAttributeValue(String attribute, String header) {
    String[] nameAndValue = attribute.split("=");
    return nameAndValue[1];
  }

  /**
   * Get the "Expires" attribute for this cookie.
   *
   * @return the "Expires" attribute for this cookie, or {@code null} if not set
   * @since 5.1.11
   */
  @Nullable
  public ZonedDateTime getExpires() {
    return this.expires;
  }

  /**
   * Set the "Expires" attribute for this cookie.
   *
   * @since 5.1.11
   */
  public void setExpires(@Nullable ZonedDateTime expires) {
    this.expires = expires;
  }

  /**
   * Get the "SameSite" attribute for this cookie.
   *
   * @return the "SameSite" attribute for this cookie, or {@code null} if not set
   */
  @Nullable
  public String getSameSite() {
    return this.sameSite;
  }

  /**
   * Set the "SameSite" attribute for this cookie.
   * <p>This limits the scope of the cookie such that it will only be attached
   * to same-site requests if the supplied value is {@code "Strict"} or cross-site requests if the
   * supplied value is {@code "Lax"}.
   *
   * @see <a href="https://tools.ietf.org/html/draft-ietf-httpbis-rfc6265bis#section-4.1.2.7">RFC6265
   * bis</a>
   */
  public void setSameSite(@Nullable String sameSite) {
    this.sameSite = sameSite;
  }


}
