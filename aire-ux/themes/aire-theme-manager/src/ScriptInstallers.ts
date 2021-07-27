import {AireThemeManager, Registration} from "./AireThemeManager";
import {ScriptDefinition} from "./Theme";

export interface ScriptRegistration extends Registration {

}

export interface ScriptInstaller {
  readonly manager: AireThemeManager;

  install(definition: ScriptDefinition): Promise<ScriptRegistration>
}


export class LocalInlineScriptInstaller implements ScriptInstaller {

  constructor(readonly manager: AireThemeManager) {

  }

  install(
      definition: ScriptDefinition
  ): Promise<ScriptRegistration> {
    let script = createElement(definition);
    return new Promise((resolve, reject) => {
      script.textContent = definition.content;
      document.head.append(script);
      resolve(new DefaultScriptRegistration(script));
    });
  }
}


export class RemoteInlineScriptInstaller implements ScriptInstaller {
  constructor(readonly manager: AireThemeManager) {

  }

  install(
      definition: ScriptDefinition
  ): Promise<ScriptRegistration> {
    const script = createElement(definition);
    script.src = definition.content;
    return new Promise((resolve, reject) => {
      script.onload = (e: Event) => {
        resolve(new DefaultScriptRegistration(script));
      }
      script.onerror = (e: string | Event) => {
        reject(new Error(`Error loading script.  Reason: ${e}`));
      }
      document.head.append(script);
    });


  }
}

function createElement(definition: ScriptDefinition): HTMLScriptElement {
  let element = document.createElement('script');
  element.type = 'text/javascript';
  if (definition.defer) {
    element.defer = definition.defer;
  }
  if (definition.asynchronous) {
    element.async = definition.asynchronous;
  }
  if (definition.integrity) {
    element.integrity = definition.integrity;
  }
  return element;
}

class DefaultScriptRegistration implements ScriptRegistration {

  constructor(readonly element: HTMLScriptElement) {

  }

  remove(): void {
    this.element.remove();
  }

}