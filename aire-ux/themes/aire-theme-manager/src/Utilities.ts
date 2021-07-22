import {PageStyleDefinitionProperties} from "./PageStyleDefinition";

/**
 *
 * @param url the url to load text from
 * @param method the method
 */
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
export function constructStyleSheetFrom(properties: PageStyleDefinitionProperties): Promise<CSSStyleSheet> {
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
