import org.gradle.api.Project;

class AireComponent {
    final Project project;
    private boolean component;

    AireComponent(Project project) {
        this.project = project;
    }


    void setComponent(boolean component) {
        this.component = component
        project.afterEvaluate {
            project.with {
                apply plugin: 'com.github.node-gradle.node'

                project.task(type: NpmTask, 'bundle', {
                    args = ['run', 'bundle', '--no-warnings']
                })

                /**
                 * install npm before building
                 */
                bundle.dependsOn npmInstall
                bundle.mustRunAfter npmInstall


                /**
                 *
                 */
                build.dependsOn bundle
                build.mustRunAfter bundle
                test.dependsOn build
                build.mustRunAfter test
            }
        }
    }

    void setControl(boolean control) {
        if (control) {
            project.afterEvaluate {
                project.with {
                    jar {

                        exclude(
                                'META-INF/*.SF',
                                'META-INF/*.DSA',
                                'META-INF/*.RSA',
                                'META-INF/*.MF'
                        )
                        configurations.aireComponent.files.each {
                            from(zipTree(it))
                        }
                    }
                }
            }
        }
    }
}