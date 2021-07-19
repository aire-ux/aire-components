import nodeResolve from '@rollup/plugin-node-resolve'
import postcss from 'rollup-plugin-postcss'
import typescript from '@rollup/plugin-typescript'
import postcssLit from 'rollup-plugin-postcss-lit';
// import {postcss as postCssImport} from "postcss-import"
// import {postcss as postCssVarFallback} from "postcss-varfallback"
import cssNano from "cssnano"

export default {

  external: ['lit-html'],
  input: "./index.ts",
  output: {
    dir: 'dist',
    format: 'es'
  },
  plugins: [

    nodeResolve({
      browser: true,
      jsNext: true,
      mainFields: ['module', 'main', 'browser']
    }),

    postcss({
      inject: false,
      plugins: [
        require('postcss-import'),
        require('postcss-varfallback'),
        require('postcss-dropunusedvars'),
        require('cssnano')
      ]
    }),

    postcssLit(),

    typescript({
      tsconfig: './tsconfig.json'
    }),

  ]
}