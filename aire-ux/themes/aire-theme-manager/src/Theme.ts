import {Mode, Source} from "./PageStyleDefinition";

export type ThemeDefinitionElement = {

  /**
   * the source of this theme definition element
   * can be actual, textual content (e.g. the executable
   * content of a javascript) or CSS styles
   *
   * or it may be a URL
   */
  readonly source?: Source;
  /**
   * the URL or textual content of the script,
   * depending on the type
   */
  readonly content: string;

  /**
   * the hash of the theme element--applicable
   * to "page" and "constructable" stylesheets
   * as well as all scripts
   *
   */
  readonly integrity?: string

}
/**
 * define a script to be included on the page
 */
export type ScriptDefinition = ThemeDefinitionElement & {

  /**
   * defer executing the script
   * until the page has finished parsing
   * defaults to "true"
   */
  readonly defer?: boolean;

  /**
   * apply asynchronously--the script will
   * be executed while the page is parsing
   *
   * this is almost never correct, but could be used
   * sometimes
   */
  readonly asynchronous?: boolean;


};


/**
 * style definition element
 */
export type StyleDefinition = ThemeDefinitionElement & {

  /**
   * the mode to install the style definition in
   */
  readonly mode?: Mode

}


/**
 * defines element-selectors
 */
export type Applicability = {

  /**
   * apply to any elements with attributes that match the provided
   * values
   */
  readonly matchingAttributeValues?: Array<[string, string]>

  /**
   * applies to elements matching the provided
   * tag-names
   */
  readonly matchingTagNames?: Array<string>;

  /**
   * apply this theme to elements matching
   * the provided query-selectors
   *
   * this is usually only relevant to elements
   * that are themed via constructable stylesheets
   */
  readonly matchingQuerySelectors?: Array<string>;

  /**
   * match any elements with the attributes;
   */
  readonly matchingAttributeExistence?: Array<string>;

}

/**
 * hints as to how to install this theme
 *
 * will be available in the ThemeManager's current theme
 */
export type InstallationInstructions = {

  readonly applicableTo?: Applicability


  /**
   * theme-specific properties
   * should be interpreted by scripts loaded
   * by the theme manager
   */
  readonly themeProperties: Map<string, String>
}

/**
 * theme definition--holds references to
 * 1. Scripts: all of the scripts and their related types
 * 2. Styles: all of the styles to be installed
 */
export type ThemeDefinition = {


  /**
   * the scripts to install
   */
  readonly scripts: Array<ScriptDefinition>

  /**
   * the styles to install
   */
  readonly styles: Array<StyleDefinition>

  /**
   * additional installation instructions
   * everything is theme-specific
   */
  readonly installationInstructions: InstallationInstructions;
}