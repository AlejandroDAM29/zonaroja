plugins {
    id("com.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "alejandro.developer.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.transport.runtime)
    implementation(libs.hilt.android)
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.logging.interceptor)
    ksp(libs.hilt.compiler)

    //Testing
    testImplementation(libs.junit)
}
