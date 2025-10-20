plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.educacionit.biciya"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.educacionit.biciya"
        minSdk = 24
        targetSdk = 36
        versionCode = 10000
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CLIENT_ID", "\"6171bef2d0784803b9152fdfc4c983b4\"")
        buildConfigField("String", "CLIENT_SECRET", "\"9A471c0F37504d7Bb52171e474931D26\"")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isDebuggable = false
            buildConfigField("String", "CLIENT_ID", "\"asdfasfasdf\"")
            buildConfigField("String", "CLIENT_SECRET", "\"asdfasdfasdf\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += listOf("monetization")
    productFlavors {
        create("free") {
            dimension = "monetization"
            applicationIdSuffix = ".free"
            versionCode = 10000
            versionName = "1.0.0"
        }

        create("paid") {
            dimension = "monetization"
            applicationIdSuffix = ".paid"
            versionCode = 30002
            versionName = "3.0.2"

        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    implementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    //Room
    implementation("androidx.room:room-runtime:2.8.2")
    ksp("androidx.room:room-compiler:2.8.2")

    //Lottie
    implementation(libs.lottie)

    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.+")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.+")

    // Coroutines testing
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.+")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}