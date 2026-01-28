plugins {
    id("com.android.library")
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

}


android {
    namespace = "alejandro.developer.data"
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
    implementation(project(":domain"))
    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    implementation(libs.androidx.browser)
    implementation(libs.firebase.auth)
    ksp(libs.hilt.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics)
// Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)

    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.firebase.auth)

    //Testing
    testImplementation(libs.junit)
}
