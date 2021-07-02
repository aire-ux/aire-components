# Aire Theme Manager 

This component is intended to be used by the Aire UX Theme Manager component.  Nonetheless, it
can be a useful way to alter styles within the DOM and respond to them


## Installation
```bash
npm i @aire-ux/aire-theme-manager
```

## Usage
Aire ThemeManager supports two types of styling changes out of the box:

### Dynamic Page Inclusions
This is the simplest type of theming: the theme manager dynamically adds
and removes `<link>` tags to the page for your components' consumption.
For better or worse, the CSS isolation of shadow-dom prevents this
styling from propagating to web-component based components.


```typescript
  import {Aire} from '@aire-ux/aire-theme-manager'

  function setBootstrap() {
    Aire.uninstallStyles();
    Aire.installStyles([{
      id: 'bootstrap',
      url: 'https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css',
      mode: Aire.Mode.Global
    }])
  }

  function setMaterial() {
    Aire.uninstallStyles();
    Aire.installStyles([{
      id: 'mdb',
      url: 'https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/3.6.0/mdb.min.css',
      mode: Aire.Mode.Global
    }])
  }

```

### Adopted Stylesheets (Shadow DOM)
This mode is the default for Aire components. Changes are propagated via
events to all listening components (
    registered via the 
    `@dynamicallyThemeable` decorator exported by `@aire-ux/aire-theme-decorators`)


```typescript

function setBootstrap() {
  Aire.uninstallStyles();
  Aire.installStyles([{
    id: 'bootstrap',
    url: 'https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css',
    mode: Aire.Mode.Constructable
  }])
}


function setMaterial() {
  Aire.uninstallStyles();
  Aire.installStyles([{
    id: 'mdb',
    url: 'https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/3.6.0/mdb.min.css',
    mode: Aire.Mode.Constructable
  }])
}
```






## Linting with ESLint, Prettier, and Types
To scan the project for linting errors, run
```bash
npm run lint
```

You can lint with ESLint and Prettier individually as well
```bash
npm run lint:eslint
```
```bash
npm run lint:prettier
```

To automatically fix many linting errors, run
```bash
npm run format
```

You can format using ESLint and Prettier individually as well
```bash
npm run format:eslint
```
```bash
npm run format:prettier
```

## Testing with Web Test Runner
To run the suite of Web Test Runner tests, run
```bash
npm run test
```

To run the tests in watch mode (for &lt;abbr title=&#34;test driven development&#34;&gt;TDD&lt;/abbr&gt;, for example), run

```bash
npm run test:watch
```

## Demoing with Storybook
To run a local instance of Storybook for your component, run
```bash
npm run storybook
```

To build a production version of Storybook, run
```bash
npm run storybook:build
```


## Tooling configs

For most of the tools, the configuration is in the `package.json` to reduce the amount of files in your project.

If you customize the configuration a lot, you can consider moving them to individual files.

## Local Demo with `web-dev-server`
```bash
npm start
```
To run a local development server that serves the basic demo located in `demo/index.html`
