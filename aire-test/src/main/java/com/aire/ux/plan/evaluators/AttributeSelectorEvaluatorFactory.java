package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import io.sunshower.lambda.Option;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.val;

public class AttributeSelectorEvaluatorFactory implements EvaluatorFactory {

  static final String WS = "\\s+";

  @Override
  public Symbol getEvaluationTarget() {
    return ElementSymbol.AttributeSelector;
  }

  @Override
  public Evaluator create(SyntaxNode<Symbol, Token> node, PlanContext context) {
    return new AttributeSelectorEvaluator(node, context);
  }

  private static class AttributeSelectorEvaluator implements Evaluator {


    @Nullable
    private final String value;
    @Nonnull
    private final String attributeName;

    @Nullable
    private final ElementSymbol combinator;


    public AttributeSelectorEvaluator(
        SyntaxNode<Symbol, Token> node, PlanContext context) {
      val children = node.getChildren();
      if (children.isEmpty()) {
        throw new IllegalArgumentException(
            "Somehow the parser did not catch an empty attribute selector list (node: %s)"
                .formatted(node));
      }
      /**
       * prevent children from narrowing further epochs
       */
      val results = node.clearChildren();
      attributeName = results.get(0).getSource().getLexeme();
      if (results.size() == 3) {
        value = normalize(results.get(2).getSource().getLexeme());
        combinator = (ElementSymbol) results.get(1).getSymbol();
      } else {
        value = null;
        combinator = null;
      }
    }

    static String normalize(String input) {
      val ch = input.charAt(0);
      if (ch == '\'' || ch == '"') {
        return input.substring(1, input.length() - 1);
      }
      return input;
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      val result = new LinkedHashSet<T>();
      for (val element : workingSet) {
        hom.reduce(element, result, (t, u) -> {
          if (combinator == null) {
            u.addAll(selectAttributeExistance(t, hom));
          } else {
            u.addAll(selectAttributeMatching(t, hom));
          }
          return u;
        });
      }
      return result;
    }

    private <T> Collection<T> selectAttributeExistance(T element, NodeAdapter<T> hom) {
      if (hom.hasAttribute(element, attributeName)) {
        return Option.of(element);
      }
      return Option.none();
    }


    private <T> Option<T> selectAttributeMatching(T element, NodeAdapter<T> hom) {
      val attribute = hom.getAttribute(element, attributeName);
      if (attribute == null) {
        return Option.none();
      }
      switch (combinator) {
        case StrictEquality: {
          if (Objects.equals(attribute, value)) {
            return Option.of(element);
          }
          break;
        }

        case Includes: {
          val values = attribute.split(WS);
          for (val value : values) {
            if (Objects.equals(this.value, value)) {
              return Option.of(element);
            }
          }
          break;
        }

        case DashMatch: {
          if (Objects.equals(value, attribute) || attribute.startsWith(value + "-")) {
            return Option.of(element);
          }
          break;
        }

        case PrefixMatch: {
          val values = attribute.split(WS);
          for (val value : values) {
            if (value.startsWith(this.value)) {
              return Option.of(element);
            }
          }
          break;
        }

        case SuffixMatch: {
          val values = attribute.split(WS);
          for (val value : values) {
            if (value.endsWith(this.value)) {
              return Option.of(element);
            }
          }
          break;
        }

        case SubstringMatch: {
          val values = attribute.split(WS);
          for (val value : values) {
            if (value.contains(this.value)) {
              return Option.of(element);
            }
          }
          break;
        }
      }
      return Option.none();
    }
  }
}
