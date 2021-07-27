/**
 *
 * @param url the url to load text from
 * @param method the method
 */
import {InstallationInstructions, StyleDefinition} from "./Theme";

export function loadText(url: string, method: string = 'GET'): Promise<string> {
  return new Promise((resolve, reject) => {
    const request = new XMLHttpRequest();
    request.onload = () => {
      if (request.status >= 200 && request.status < 300) {
        resolve(request.responseText);
      } else {
        reject(
            new Error(`
              Failed to load value at ${url} via method ${method}. Reason: ${request.statusText} (code: ${request.status}
            `)
        );
      }
    }
    request.onerror = () => {
      reject(
          new Error(`
              Failed to load value at ${url} via method ${method}. Reason: ${request.statusText} (code: ${request.status}
            `)
      );
    }
    request.open(method, url);
    request.send();
  });

}

/**
 * walk the dom
 * does not risk a stack overflow error
 * @param from the element to start the traversal at
 * @param f the function to apply to each element
 * @param t a cast/filter function to transform the element to something consumable
 * if t returns null for an element, traversal to its children is not halted
 */

export function walkDom<T = Element>(
    from: Element = document.documentElement,
    f: (e: T) => void,
    t: (e: Element) => T | null = (e: Element) => e as unknown as T
) {
  const stack = [from] as Array<Element>;
  while (stack.length) {
    const el = stack.pop() as Element,
        e = t(el);
    if (e) {
      f(e);
    }
    stack.push(...Array.from(el.children));
  }
}


/**
 *
 * @param properties the properties to load a stylesheet from
 */
export function constructStyleSheetFrom(properties: StyleDefinition): Promise<CSSStyleSheet> {
  const textLoader = properties.urlLoader ?? loadText;
  return new Promise((resolve, reject) => {
    textLoader(properties.content, 'GET').then(
        styleDefinition => {
          const stylesheet = new CSSStyleSheet() as CSSStyleSheet & {
            replace(definition: string): Promise<StyleSheet>
          };
          stylesheet.replace(styleDefinition).then(_ => {
            resolve(stylesheet)
          })
        });
  });
}

/**
 *
 * @param installationInstructions the installation instructions to collect the elements over
 */
export function collectElements(installationInstructions: InstallationInstructions) {
  const applicableTo = installationInstructions.applicableTo,
      elements: Element[] = [];
  if (!applicableTo) {
    return;
  }
  const predicates: Array<(e: Element) => boolean> = [];
  if (applicableTo.matchingAttributeExistence) {
    const attrs = applicableTo.matchingAttributeExistence;
    predicates.push((el: Element) => {
      for (const attribute of attrs) {
        if (el.hasAttribute(attribute)) {
          return true;
        }
      }
      return false;
    });
  }

  if (applicableTo.matchingAttributeValues) {
    const attrs = applicableTo.matchingAttributeValues;
    predicates.push((el: Element) => {
      for (const [key, value] of attrs) {
        if (el.getAttribute(key) === value) {
          return true;
        }
      }
      return false;
    });
  }

  const matches = (element: Element) => {
    for (const predicate of predicates) {
      if (predicate(element)) {
        return true;
      }
    }
    return false;
  }


  walkDom(document.documentElement,
      (element: Element) => {
        if (matches(element)) {
          elements.push(element);
        }
      })

  if (applicableTo.matchingQuerySelectors) {
    const joinedSelector = applicableTo.matchingQuerySelectors.join(",")
    elements.push(...Array.from(document.querySelectorAll(joinedSelector)));
  }

  if (applicableTo.matchingTagNames) {
    const joinedSelector = applicableTo.matchingTagNames.join(",")
    elements.push(...Array.from(document.querySelectorAll(joinedSelector)));
  }
  return elements;
}
