import terser from 'rollup-plugin-terser'
import commonjs from 'rollup-plugin-commonjs'
import postcss from 'rollup-plugin-postcss'
import nodeResolve from '@rollup/plugin-node-resolve'
import typescript from '@rollup/plugin-typescript'
import postcssLit from 'rollup-plugin-postcss-lit';

export default {

  external: ['lit-html', 'lit-element', 'mxGraph'],
  input: "./src/index.ts",
  output: {
    // dir: 'dist',
    format: 'iife',
    file: 'dist/iife/aire-designer.min.js'
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
    commonjs(),
    terser.terser({
      output: {
        comments: false
      }
    })
  ]
}