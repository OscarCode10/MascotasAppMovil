plugins {
    id("com.android.application")
}

android {
    namespace = "com.umb.mascotasapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.umb.mascotasapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Alinea kotlin-stdlib-jdk7/jdk8 con kotlin-stdlib 1.8.22 (evita "Duplicate class found")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.22"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    // Biblioteca HTTP indicada en la guía
    implementation("com.android.volley:volley:1.2.1")
}
