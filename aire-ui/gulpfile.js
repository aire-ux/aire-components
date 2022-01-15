const gulp = require('gulp'),
    scss = require('gulp-sass')(require('node-sass'));

gulp.task('scss:build', (done) => {
  return gulp.src('./src/main/styles/**/*.scss')
      .pipe(scss({outputStyle: 'compressed'})).on('error', scss.logError)
      .pipe(gulp.dest('./frontend/styles'))
})

gulp.task('scss:dev', (done) => {
  return gulp.src('./src/main/styles/**/*.scss')
      .pipe(scss()).on('error', scss.logError)
      .pipe(gulp.dest('./frontend/styles'))
})

gulp.task('scss:watch', () => {
  gulp.watch('./src/main/styles/**/*.scss', gulp.series('scss:dev'))
});
