plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val exposedVersion: String by project
val postgresVersion: String by project
val rabbitVersion: String by project

dependencies {
    implementation(project(":components:rabbit-support"))
    implementation(project(":components:serialization-support"))

    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    testImplementation(project(":components:test-support"))
    testImplementation("org.postgresql:postgresql:$postgresVersion")
}
