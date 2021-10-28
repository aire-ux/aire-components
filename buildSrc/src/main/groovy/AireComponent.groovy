import org.gradle.api.Project;

class AireComponent {
    final Project project;

    AireComponent(Project project) {
        this.project = project;
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