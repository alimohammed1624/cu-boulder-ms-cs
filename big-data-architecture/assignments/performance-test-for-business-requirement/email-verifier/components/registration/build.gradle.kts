plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project

dependencies {
    implementation(project(":components:registration-request"))
    implementation(project(":components:serialization-support"))

    implementation("io.ktor:ktor-server-resources:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    testImplementation("org.postgresql:postgresql:$postgresVersion")
}
