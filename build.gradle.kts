// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id ("com.android.library")  version "8.0.1" apply false
    id("com.google.dagger.hilt.android") version Versions.hilt apply false
    id("androidx.navigation.safeargs.kotlin") version Versions.navVersion apply false
}




/*
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}*/
