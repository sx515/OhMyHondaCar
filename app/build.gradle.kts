plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dicklight.ohmyhondacar"
    compileSdk = 35
    ndkVersion = "21.4.7075529"

    defaultConfig {
        applicationId = "com.dicklight.ohmyhondacar"
        minSdk = 17
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {


    }
    buildFeatures {
        viewBinding = false
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {
    compileOnly(files("C:\\Users\\sxwr2\\Desktop\\StrgSW.jar"))
    implementation("com.google.code.gson:gson:2.8.8")
}