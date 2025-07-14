plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ndb"
    compileSdk = 36

    defaultConfig {
        applicationId = "kanana.notesdatabase"
        minSdk = 21         //todo: was 17 but changed it to 21 to FIX error" Cannot fit requested classes in a single dex file (# methods: 68327 > 65536)"
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\gson-2.1.jar"))
    implementation(files("libs\\ant-1.7.0.jar"))
    implementation(files("libs\\dslv.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}