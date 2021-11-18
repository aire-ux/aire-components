import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

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
            }
        }
    }

    void setControl(boolean control) {
        if (control) {
            project.afterEvaluate {
                project.with {

                    tasks.create(name: 'copyAireToBuildDirectory', type:Copy) {
                        configurations.aireComponent.files.each {
                            from(zipTree(it))
                            into file("${project.buildDir}/resources/main")
                        }
                    }

                    jar {
                        exclude(
                                'META-INF/*.SF',
                                'META-INF/*.DSA',
                                'META-INF/*.RSA',
                                'META-INF/*.MF'
                        )

                    }
                    build.dependsOn('copyAireToBuildDirectory')
                    build.mustRunAfter('copyAireToBuildDirectory')
                }
            }
        }
    }
}