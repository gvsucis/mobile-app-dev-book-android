plugins {
    id("com.android.application")
}

android {
    namespace = "edu.gvsu.cis.traxy"

    compileSdk = 31

    defaultConfig {
        applicationId = "edu.gvsu.cis.traxy"
        minSdk = 24
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.3")
    implementation("com.google.android.material:material:1.2.1")
}
