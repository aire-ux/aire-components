package com.aire.ux.theme;

import io.sunshower.lang.common.encodings.Encoding;
import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import io.sunshower.lang.common.hash.Hashes;
import io.sunshower.lang.common.hash.Hashes.Algorithm;
import io.sunshower.lang.common.hash.Hashes.HashCode;
import java.util.Locale;

public interface ResourceDefinition {

  HashCode hashcode = Hashes.hashCode(Algorithm.MD5);

  /** */
  Encoding encoding = Encodings.create(Type.Base58);

  enum Source {
    remote,
    inline;

    /** shitty serialization makes it impossible to override them names */
    public static final Source Inline = Source.inline;

    public static final Source Remote = Source.remote;

    public String toString() {
      return name().toLowerCase(Locale.ROOT);
    }

    public Source fromValue(String value) {
      if (value == null) {
        throw new IllegalArgumentException("No source corresponding to 'null'");
      }
      switch (value.toLowerCase(Locale.ROOT)) {
        case "inline":
          return Source.inline;
        case "remote":
          return Source.remote;
      }
      throw new IllegalArgumentException("No source corresponding to value '" + value + "'");
    }
  }
}
