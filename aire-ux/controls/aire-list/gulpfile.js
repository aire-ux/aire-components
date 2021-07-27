const gulp = require('gulp'),
    csslit = require('gulp-csslit'),
    rename = require('gulp-rename'),
    sass = require('gulp-dart-sass');

const themes = [
  'base',
  'bootstrap',
  'material',
  'spectrum',
  'uikit',
  'aire'
]


const themeTasks = themes.map(theme => {
  const name = `build:theme:${theme}`;
  gulp.task(name, () => {
    return gulp.src(`src/themes/${theme}/aire-list.scss`)
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

gulp.task('build:themes', gulp.parallel(...themeTasks));

gulp.task('themes:build:watch', () => {
  gulp.watch('./src/themes/**/*.scss', gulp.series('build:themes'))
});

// exports.build = gulp.parallel(...themeTasks);
// exports['build:watch'] =

