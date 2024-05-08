plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "io.initialcapacity.emailverifier"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation(kotlin("test-junit"))
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs +
            "-opt-in=kotlin.time.ExperimentalTime"
    }
}
