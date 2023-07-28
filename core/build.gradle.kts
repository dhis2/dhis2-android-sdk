/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
    id("org.jetbrains.dokka") version "1.7.20" apply false
}

apply(from = project.file("plugins/android-checkstyle.gradle"))
apply(from = project.file("plugins/android-pmd.gradle"))
apply(from = project.file("plugins/jacoco.gradle"))
apply(from = project.file("plugins/gradle-mvn-push.gradle"))

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

val _targetSdkVersion = 33
val _minSdkVersion = 21
val VERSION_CODE: String by project
val VERSION_NAME: String by project

/*
** Libraries
*/
val libraryDesugaring = "1.2.2"

// android
val annotation = "1.4.0"
val paging = "2.1.2"

// java
val jackson = "2.13.4"
val autoValue = "1.10.1"
val autoValueCursor = "2.0.1"
val retrofit = "2.9.0"
val okHttp = "3.14.9"
val dagger = "2.44.2"
val rxJava = "2.2.21"
val rxAndroid = "2.1.1"
val sqlCipher = "4.4.3"
val smsCompression = "0.2.0"
val expressionParser = "1.0.33"

// Kotlin
val kotlinxDatetime = "0.4.0"
val coroutines = "1.6.4"

// test dependencies
val coreTesting = "2.2.0"
val jUnit = "4.13.2"
val mockito = "3.4.6"
val mockitoKotlin = "2.2.0"
val truth = "1.1.3"
val testRunner = "1.5.2"
val testRules = "1.5.0"
val equalsVerifier = "3.14"
val flipper = "0.83.0"
val soloader = "0.10.5"
val liveDataTesting = "1.3.0"

// open id
val appauth = "0.8.1"

android {
    compileSdk = _targetSdkVersion

    defaultConfig {
        minSdk = _minSdkVersion
        targetSdk = _targetSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        buildConfigField("long", "VERSION_CODE", VERSION_CODE)
        buildConfigField("String", "VERSION_NAME", "\"${VERSION_NAME}\"")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources {
            excludes += listOf("META-INF/LICENSE", "META-INF/rxjava.properties")
        }
    }

    buildTypes {
        getByName("debug") {
            // a fix for the debugger not being able to find local scope variables
            enableAndroidTestCoverage = (project.hasProperty("coverage"))
        }
    }

    sourceSets {
        sourceSets.getByName("test") {
            resources.srcDirs("src/sharedTest/resources")
        }
        sourceSets.getByName("androidTest") {
            java.srcDirs("src/sharedTest/java")
            resources.srcDirs("src/sharedTest/resources")
        }
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
    }

    lint {
        abortOnError = true
        disable += "MissingTranslation"
        warning += "InvalidPackage"
    }

    namespace = "org.hisp.dhis.android"
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:$libraryDesugaring")

    // RxJava
    api("io.reactivex.rxjava2:rxjava:$rxJava")
    api("io.reactivex.rxjava2:rxandroid:$rxAndroid")

    // AndroidX
    api("androidx.annotation:annotation:$annotation")
    api("androidx.paging:paging-runtime:$paging")

    // Auto Value
    api("com.google.auto.value:auto-value-annotations:$autoValue")
    kapt("com.google.auto.value:auto-value:$autoValue")

    // Dagger
    api("com.google.dagger:dagger:$dagger")
    kapt("com.google.dagger:dagger-compiler:$dagger")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind:$jackson")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson")

    // Square libraries
    api("com.squareup.okhttp3:okhttp:$okHttp")
    api("com.squareup.retrofit2:retrofit:$retrofit")
    api("com.squareup.retrofit2:converter-jackson:$retrofit")
    api("com.squareup.retrofit2:adapter-rxjava2:$retrofit")

    // Kotlin
    api("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetime")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:$coroutines")

    // sms compression library
    api("com.github.dhis2:sms-compression:$smsCompression")

    // DHIS 2 antlr expression parser
    api("org.hisp.dhis.parser:dhis-antlr-expression-parser:$expressionParser")

    // Extension which generates mappers for work with cursor and content values
    api("com.gabrielittner.auto.value:auto-value-cursor-annotations:$autoValueCursor")
    kapt("com.gabrielittner.auto.value:auto-value-cursor:$autoValueCursor")

    api("net.zetetic:android-database-sqlcipher:$sqlCipher")

    api("com.squareup.okhttp3:mockwebserver:$okHttp")

    // Java test dependencies
    testImplementation("junit:junit:$jUnit")
    testImplementation("org.mockito:mockito-core:$mockito")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlin")
    testImplementation("com.google.truth:truth:$truth") {
        exclude(group = "junit") // Android has JUnit built in.
    }

    testImplementation("nl.jqno.equalsverifier:equalsverifier:$equalsVerifier")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okHttp")
    testImplementation("androidx.test:runner:$testRunner")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines")

    // Android test dependencies
    androidTestImplementation("org.mockito:mockito-core:$mockito")
    androidTestImplementation("com.jraska.livedata:testing-ktx:$liveDataTesting")
    androidTestImplementation("androidx.arch.core:core-testing:$coreTesting")
    androidTestImplementation("androidx.test:runner:$testRunner")
    androidTestImplementation("androidx.test:rules:$testRules")
    androidTestImplementation("com.squareup.okhttp3:logging-interceptor:$okHttp")
    androidTestImplementation("com.google.truth:truth:$truth") {
        exclude(group = "junit") // Android has JUnit built in.
    }

    debugImplementation("com.facebook.flipper:flipper:$flipper")
    debugImplementation("com.facebook.soloader:soloader:$soloader")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:$flipper") {
        exclude(group = "com.squareup.okhttp3")
    }

    releaseImplementation("com.facebook.flipper:flipper-noop:$flipper")

    implementation("net.openid:appauth:$appauth")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
}

detekt {
    toolVersion = "1.18.0"
    config = files("config/detekt.yml")
    parallel = true
    buildUponDefaultConfig = false
}
