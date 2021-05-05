package com.aire.ux.plan.evaluators;

import com.aire.ux.parsers.ast.Symbol;
import com.aire.ux.parsers.ast.SyntaxNode;
import com.aire.ux.plan.Evaluator;
import com.aire.ux.plan.EvaluatorFactory;
import com.aire.ux.plan.PlanContext;
import com.aire.ux.select.css.CssSelectorParser.ElementSymbol;
import com.aire.ux.select.css.Token;
import com.aire.ux.test.NodeAdapter;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.val;

public class AttributeSelectorEvaluatorFactory implements EvaluatorFactory {

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
      if(ch == '\'' || ch == '"') {
        return input.substring(1, input.length() - 1);
      }
      return input;
    }

    @Override
    public <T> Set<T> evaluate(Set<T> workingSet, NodeAdapter<T> hom) {
      if (combinator == null) {
        return selectAttributeExistance(workingSet, hom);
      } else {
        return selectAttributeMatching(workingSet, hom);
      }
    }

    private <T> Set<T> selectAttributeExistance(Set<T> workingSet, NodeAdapter<T> hom) {
      val results = new LinkedHashSet<T>();
      for (val element : workingSet) {
        if (hom.hasAttribute(element, attributeName)) {
          results.add(element);
        }
      }
      return results;
    }

    private <T> Set<T> selectAttributeMatching(Set<T> workingSet, NodeAdapter<T> hom) {
      val results = new LinkedHashSet<T>();
      for (val element : workingSet) {
        if (hom.hasAttribute(element, attributeName)) {
          checkAttributeValue(element, results, hom);
        }
      }
      return results;
    }

    private <T> void checkAttributeValue(T element, LinkedHashSet<T> results, NodeAdapter<T> hom) {
      val attribute = hom.getAttribute(element, attributeName);
      if (attribute == null) {
        return;
      }
      switch (combinator) {
        case StrictEquality: {
          val values = attribute.split("\\s+");
          for(val value : values) {
            if(Objects.equals(value, this.value)) {
              results.add(element);
              return;
            }
          }
          break;
        }

        case Includes: {
          val values = attribute.split("\\s+");
          for (val value : values) {
            if (Objects.equals(this.value, value)) {
              results.add(element);
              return;
            }
          }
          break;
        }

        case DashMatch: {
          if (Objects.equals(value, attribute) || attribute.startsWith(value + "-")) {
            results.add(element);
          }
          break;
        }

        case PrefixMatch: {
          val values = attribute.split("\\s+");
          for(val value : values) {
            if(value.startsWith(this.value)) {
              results.add(element);
              return;
            }
          }
          break;
        }

        case SuffixMatch: {
          val values = attribute.split("\\s+");
          for(val value : values) {
            if(value.endsWith(this.value)) {
              results.add(element);
              return;
            }
          }
          break;
        }

        case SubstringMatch: {
          val values = attribute.split("\\s+");
          for(val value : values) {
            if(value.contains(this.value)) {
              results.add(element);
              return;
            }
          }
          break;
        }
      }
    }


  }
}
