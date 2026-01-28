plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // Coroutines (opcional pero habitual)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)


    // Unit tests (JVM)
    testImplementation(libs.junit)
}
