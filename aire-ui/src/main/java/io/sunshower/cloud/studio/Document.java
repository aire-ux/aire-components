package io.sunshower.cloud.studio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.val;
import org.apache.commons.io.input.CharSequenceInputStream;

public interface Document {

  static void write(Document document, CharSequence text) throws IOException {
    document.write(new CharSequenceInputStream(text, StandardCharsets.UTF_8));
  }

  static CharSequence read(Document document) throws IOException {
    val outputStream = new ByteArrayOutputStream();
    try (val inputStream = document.read()) {
      inputStream.transferTo(outputStream);
    }
    return outputStream.toString(StandardCharsets.UTF_8);
  }

  Document checkout();

  Document checkout(String branchName);

  InputStream read();

  void write(InputStream source);

  Revision commit(String commitMessage);

  void checkout(Revision revision);

  List<Revision> getRevisions();
}
