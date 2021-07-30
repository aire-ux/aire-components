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

gulp.task('build:material', () => {
  return gulp.src(`material/material.scss`)
      .pipe(sass().on('error', sass.logError))
      .pipe(gulp.dest(`dist/material/`))
      .pipe(csslit())
      .pipe(rename({
        extname: '.js'
      }))
      .pipe(gulp.dest(`themes/material/`));

});


gulp.task(
    'build:themes',
    gulp.parallel('build:material')
);

gulp.task('themes:build:watch', () => {
  gulp.watch('./src/themes/**/*.scss', gulp.series('build:themes'))
});

// exports.build = gulp.parallel(...themeTasks);
// exports['build:watch'] =

