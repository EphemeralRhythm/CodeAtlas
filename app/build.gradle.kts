plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}



android {
    namespace = "com.example.codeatlas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.codeatlas"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.firebase:firebase-storage:21.0.0")
    implementation(libs.activity)
    //implementation("com.android.support:design:28.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    implementation ("com.google.guava:guava:29.0-android")
    implementation("androidx.work:work-runtime:2.7.1")

    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("androidx.fragment:fragment:1.3.6")

    //implementation(libs.json)
    implementation("io.noties.markwon:core:4.6.2")
    implementation ("de.hdodenhof:circleimageview:2.1.0")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    implementation ("com.airbnb.android:lottie:4.2.2")


}
