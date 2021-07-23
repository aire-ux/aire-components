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


export function walkDom<T>(
    from: Element = document.documentElement,
    f: (e: T) => void,
    t: (e: Element) => T | null
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
          console.log("Found matching attribute existence element", el)
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
          console.log("Found matching attribute value element", el)
          return true;
        }
      }
      return false;
    });
  }

  const matches = (element: Element) => {
    for(const predicate of predicates) {
      if(predicate(element)) {
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
      }, (el: Element) => (el))

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
