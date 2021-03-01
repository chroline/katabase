plugins {
  kotlin("jvm") version "1.4.31"
  kotlin("plugin.serialization") version "1.4.31"
}

group = "org.example"
version = "1.0.0"

repositories {
  mavenCentral()
  jcenter()
  maven("https://jitpack.io")
}

dependencies {
  implementation(kotlin("stdlib"))

  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.0.1")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  implementation(kotlin("reflect"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "13"
    freeCompilerArgs += listOf(
      "-Xopt-in=kotlinx.serialization.InternalSerializationApi",
      "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi"
    )
  }
}

