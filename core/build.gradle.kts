plugins {
    id("com.android.library")
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

    //Testing
    testImplementation(libs.junit)
}
