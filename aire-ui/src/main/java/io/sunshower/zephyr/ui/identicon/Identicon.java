package io.sunshower.zephyr.ui.identicon;

import com.vaadin.flow.component.html.Image;
import io.sunshower.arcus.identicon.Jdenticon;
import io.sunshower.lang.common.encodings.Encoding;
import io.sunshower.lang.common.encodings.Encodings;
import io.sunshower.lang.common.encodings.Encodings.Type;
import lombok.val;

public class Identicon {

  static final Encoding encoding = Encodings.create(Type.Base64);
  static final String SVG_PREFIX = "data:image/svg+xml;base64,";

  public static Image createFromObject(Object data) {
    val alt = String.format("Image for data (unencoded : %s)", data);
    return createFromObject(data, alt);
  }

  public static Image createFromObject(Object data, String alt) {
    val unencoded = Jdenticon.toSvg(data);
    val img = new Image();
    img.setSrc(SVG_PREFIX + encoding.encode(unencoded));
    img.setAlt(alt);
    img.setWidth("32px");
    img.setHeight("32px");
    return img;
  }
}
