plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-android")
}

tasks.getByPath("preBuild").dependsOn("ktlintFormat")

ktlint {
    android.set(false)
    ignoreFailures.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/"))
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

android {
    namespace = "com.example.foodapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    flavorDimensions += "env"
    productFlavors {
        create("production") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"https://api-restaurant.binaracademy.org/\"",
            )
        }
        create("integration") {
            buildConfigField(
                type = "String",
                name = "BASE_URL",
                value = "\"https://api-restaurant.binaracademy.org/\"",
            )
        }
    }
}
//
// tasks.withType<Test> {
//    useJUnitPlatform() // Make all tests use JUnit 5
// }

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.coil)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.fragment.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.room.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)
    ksp(libs.room.compiler)
    implementation(libs.coroutine.core)
    implementation(libs.coroutine.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.koin.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.turbine)
    testImplementation(libs.core.testing)
    testImplementation(kotlin("test"))
}
