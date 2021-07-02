import nodeResolve from '@rollup/plugin-node-resolve'
import postcss from 'rollup-plugin-postcss'
import typescript from '@rollup/plugin-typescript'
import postcssLit from 'rollup-plugin-postcss-lit';


const IIFEBuild = {
  input: "./index.ts",
  output: {
    file: "dist/aire.iife.js",
    // dir: 'dist',
    format: 'iife',
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
      tsconfig: './tsconfig.prod.json'
    }),

  ]
}

export default [IIFEBuild]