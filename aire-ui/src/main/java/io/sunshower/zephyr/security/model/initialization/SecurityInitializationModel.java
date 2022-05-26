package io.sunshower.zephyr.security.model.initialization;

import io.sunshower.crypt.core.SecretService;
import io.sunshower.lang.common.encodings.Encoding;
import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.val;

public class SecurityInitializationModel {

  private final Encoding encoding;
  private final SecretService service;

  private String encodedParameters;

  public SecurityInitializationModel(@NonNull SecretService service, @NonNull Encoding encoding) {
    this.service = service;
    this.encoding = encoding;
    createInitialParameters();
  }

  public SecurityInitializationModel(SecretService service) {
    this(service, Encodings.create(Type.Base64));
  }

  private void createInitialParameters() {
    val set = service.createEncryptionServiceSet();
    val encodedIv = encoding.encode(set.getInitializationVector());
    val encodedSalt = encoding.encode(set.getSalt());
    encodedParameters = encoding.encode(String.format("%s:%s", encodedIv, encodedSalt));
  }

  public String getSalt() {
    return new String(getRawSalt(), StandardCharsets.UTF_8);
  }

  public byte[] getRawSalt() {
    val params = new String(encoding.decode(encodedParameters), StandardCharsets.UTF_8).split(":");
    val saltP = params[1];
    return encoding.decode(saltP);
  }

  public byte[] getRawInitializationVector() {
    val params = new String(encoding.decode(encodedParameters), StandardCharsets.UTF_8).split(":");
    val ivp = params[0];
    return encoding.decode(ivp);
  }

  public String getInitialParameters() {
    return encodedParameters;
  }
}
