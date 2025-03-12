
plugins {
    application
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
application {
    mainClass.set("com.example.ktor_backend.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.mysql.connector.java)
    implementation(libs.logback.classic)
    implementation(libs.hikaricp)  // Connection Pool
    implementation(libs.mysql.connector.java)

}


// Top-level build file where you can add configuration options common to all sub-projects/modules

