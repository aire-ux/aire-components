const gulp = require('gulp'),
    csslit = require('gulp-csslit'),
    rename = require('gulp-rename'),
    rollup = require('gulp-rollup'),
    sass = require('gulp-dart-sass'),
    typescript = require('gulp-typescript');

const themes = [
  'base',
  'bootstrap',
  'material',
  'spectrum',
  'uikit',
  'aire'
]

/**
 * theme-related tasks
 */
const themeTasks = themes.map(theme => {
  const name = `build:theme:${theme}`;
  gulp.task(name, () => {
    return gulp.src(`src/themes/${theme}/aire-designer.scss`)
        .pipe(sass().on('error', sass.logError))
        .pipe(gulp.dest(`dist/themes/${theme}/`))
        .pipe(csslit())
        .pipe(rename({
          extname: '.js'
        }))
        .pipe(gulp.dest(`dist/themes/${theme}/`));

  });
  return name;
});

/**
 * build themes
 */
gulp.task(
    'build:themes'
    , gulp.parallel(...themeTasks)
);

/**
 * watch theme files to see if any need to be rebuilt
 */
gulp.task(
    'themes:build:watch',
    () => {
      gulp.watch(
          './src/themes/**/*.scss',
          gulp.series('build:themes')
      )
    }
);

const typescriptProject =
    typescript.createProject('tsconfig.json');
/**
 * Typescript-related tasks
 */

gulp.task('build:source', () => {
  return typescriptProject
      .src()
      .pipe(typescriptProject())
      .pipe(gulp.dest('dist'));
})





