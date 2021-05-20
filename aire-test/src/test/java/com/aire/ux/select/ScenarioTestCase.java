package com.aire.ux.select;

import static com.aire.ux.test.Nodes.node;

import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.evaluators.EvaluatorFactoryTestCase;
import com.aire.ux.select.css.CssSelectorParserTest.TestCase;
import com.aire.ux.test.Node;
import com.helger.commons.io.stream.StringInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.parsers.SAXParserFactory;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ScenarioTestCase extends EvaluatorFactoryTestCase {


  @SneakyThrows
  public static Node parse(InputStream stream) {
    val factory = SAXParserFactory.newInstance();
    val parser = factory.newSAXParser();
    val reader = parser.getXMLReader();
    val handler = new Handler();
    reader.setContentHandler(handler);
    reader.parse(new InputSource(stream));
    return handler.getRoot();
  }

  public static Node parse(String filename) {
    return parse(ClassLoader.getSystemResourceAsStream(filename));
  }

  public static Node parseString(String str) {
    return parse(new StringInputStream(str, Charset.defaultCharset()));
  }

  @Override
  protected EvaluatorFactory createFactory() {
    return null;
  }


  static final class Handler extends DefaultHandler {

    final ArrayDeque<Node> nodes;
    final AtomicReference<Node> root;


    Handler() {
      nodes = new ArrayDeque<>();
      root = new AtomicReference<>();
    }

    public Node getRoot() {
      return root.get();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
      val element = node(qName);
      for (int i = 0; i < attributes.getLength(); i++) {
        val attr = attributes.getQName(i);
        val value = attributes.getValue(i);
        element.attribute(attr, value);
      }
      nodes.push(element);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      val current = nodes.pop();
      if (nodes.isEmpty()) {
        root.set(current);
      } else {
        val next = nodes.pop();
        val children = new ArrayList<Node>(next.getChildren());
        children.add(current);
        next.setChildren(children);
        nodes.push(next);
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

      val str = new String(ch, start, length).trim();
      if (!str.isBlank()) {
        nodes.push(nodes.pop().content(str));
      }
    }
  }
}
