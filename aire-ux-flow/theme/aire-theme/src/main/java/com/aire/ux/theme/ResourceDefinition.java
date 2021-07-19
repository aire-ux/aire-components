package com.aire.ux.theme;

import io.sunshower.lang.common.encodings.Encoding;
import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import io.sunshower.lang.common.hash.Hashes;
import io.sunshower.lang.common.hash.Hashes.Algorithm;
import io.sunshower.lang.common.hash.Hashes.HashCode;
import java.nio.charset.StandardCharsets;

public interface ResourceDefinition {

  HashCode hashcode = Hashes.hashCode(Algorithm.MD5);

  /**
   *
   */
  Encoding encoding = Encodings.create(Type.Base58);

  /**
   * @return the page precedence of this theme resource
   */
  int getOrder();


  /**
   * @return the page id of this theme resource (base64-encoded md5 hash). There is no security
   * implication to this ID
   */
  default String getId() {
    return encoding.encode(hashcode.digest(getUrl().getBytes(StandardCharsets.UTF_8)));
  }


  /**
   * @return the location of this theme resource.  ID defaults to md5 of the url
   */
  String getUrl();


  /**
   *
   * @return the content of this resource, if anything.  Currently unsupported
   */
  String getContent();

  /**
   * @return the page integrity hash of this resource.
   */
  String getIntegrity();



}
