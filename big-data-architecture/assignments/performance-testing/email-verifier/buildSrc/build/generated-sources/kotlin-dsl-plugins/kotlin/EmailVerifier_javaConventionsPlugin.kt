/**
 * Precompiled [email-verifier.java-conventions.gradle.kts][Email_verifier_java_conventions_gradle] script plugin.
 *
 * @see Email_verifier_java_conventions_gradle
 */
class EmailVerifier_javaConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Email_verifier_java_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
