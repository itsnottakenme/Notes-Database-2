plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ndb"
    compileSdk = 36

    defaultConfig {
        applicationId = "kanana.notesdatabase"
        minSdk = 29         //todo: was 17 but changed it to 21 to FIX error" Cannot fit requested classes in a single dex file (# methods: 68327 > 65536)"
        targetSdk = 36      //      Now changed to 29 in order for android.os.FileUtils to work
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


    ////Attemtping to fix duplicate file constant-values.html from 2 different inputs
    //todo: probably unnecessary since commenting out jdom library
    packagingOptions {
        resources.merges.add("constant-values.html")
        resources.merges.add("stylesheet.css")
        resources.merges.add("deprecated-list.html")
        resources.merges.add("allclasses-noframe.html")
        resources.merges.add("allclasses-frame.html")
        resources.merges.add("index.html")
        resources.merges.add("package-list")

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
    implementation(files("libs\\ant-launcher-1.7.0.jar"))
    implementation(files("libs\\google-api-client-1.12.0-beta.jar"))
    implementation(files("libs\\google-play-services.jar"))
    implementation(files("libs\\google-http-client-1.12.0-beta.jar"))
    implementation(files("libs/google-http-client-gson-1.12.0-beta.jar"))
    implementation(files("libs\\google-api-services-drive-v2-rev30-1.12.0-beta.jar"))
    implementation(files("libs\\google-http-client-android-1.12.0-beta.jar"))
    implementation(files("libs\\google-api-client-android-1.12.0-beta.jar"))
    implementation(files("libs\\google-oauth-client-1.12.0-beta.jar"))
    implementation(files("libs\\guava-jdk5-13.0.jar"))
    //implementation(files("libs\\jdom-1.1-javadoc.jar")) // maybe not needed beaciuse in htmlcleaner already?
    implementation(files("libs\\htmlcleaner-2.2-javadoc.jar"))
    implementation(files("libs\\htmlcleaner-2.2.jar"))
    implementation(files("libs\\jdom-1.1.jar"))
    implementation(files("libs\\jsr305-1.3.9.jar"))

    implementation("com.github.yukuku:ambilwarna:2.0.1")
    implementation(libs.androidx.junit)

    implementation("commons-io:commons-io:2.11.0")    // Use the latest version

    //testImplementation("junit:junit:4.12")      //todo: hopefully will allow tests to compile //DOES NOT WORK


    //Will it work???
    configurations {
        all {
            exclude(group = "com.google.guava", module = "listenablefuture")
        }
    }

    //configurations.implementation{excludegroup:() }
    //implementation(libs.ambilwarna.yukuku)
    //implementation(com.github.)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
/**
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
 **/