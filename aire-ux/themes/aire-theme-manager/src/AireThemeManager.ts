/**
 * a style definition contains a name and a string
 */
import {PageStyleDefinition} from "./PageStyleDefinition";

export interface Registration {
  remove() : void;
}

export default class AireThemeManager {

  async addStyleDefinition(
      styleDefinition: PageStyleDefinition
  ) : Promise<Registration> {
    return styleDefinition.install(this);
  }
}
