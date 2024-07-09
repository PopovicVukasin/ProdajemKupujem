buildscript {
    dependencies {
        classpath ("com.google.gms:google-services:4.3.15")
        classpath ("com.android.tools.build:gradle:8.1.1")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
}