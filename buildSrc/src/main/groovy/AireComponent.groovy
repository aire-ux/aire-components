import org.gradle.api.Project;
class AireComponent {
    final Project project;
    AireComponent(Project project) {
        this.project = project;
    }

    void setComponent(boolean component) {
        if(component) {
            project.with {
                apply plugin: 'java'
                jar {
                    exclude(
                            'META-INF/*.SF',
                            'META-INF/*.DSA',
                            'META-INF/*.RSA',
                            'META-INF/*.MF'
                    )

                    manifest {
                        attributes 'Class-Path': configurations
                                .aire
                                .files
                                .collect{ file ->
                                    "component/${file.name}"
                                }
                    }
                }
            }
        }
    }
}