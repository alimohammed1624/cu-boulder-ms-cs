plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    testImplementation(project(":components:test-support"))
    testImplementation(project(":components:fake-sendgrid-endpoints"))

    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
    testImplementation("org.postgresql:postgresql:$postgresVersion")
}