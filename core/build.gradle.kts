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
    id("org.jetbrains.dokka") version "1.6.10" apply false
}

apply(from = project.file("plugins/android-checkstyle.gradle"))
apply(from = project.file("plugins/android-pmd.gradle"))
apply(from = project.file("plugins/jacoco.gradle"))
apply(from = project.file("plugins/gradle-mvn-push.gradle"))

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

val _targetSdkVersion = 31
val _minSdkVersion = 21
val VERSION_CODE: String by project
val VERSION_NAME: String by project

val libraries = mapOf(
    "libraryDesugaring" to "1.2.2",

    // android
    "annotation" to "1.4.0",
    "paging" to "2.1.2",

    // java
    "jackson" to "2.11.2",
    "autoValue" to "1.7.4",
    "autoValueCursor" to "2.0.1",
    "retrofit" to "2.9.0",
    "okHttp" to "3.12.0",
    "dagger" to "2.44.2",
    "rxJava" to "2.2.12",
    "rxAndroid" to "2.1.1",
    "sqlCipher" to "4.4.3",
    "smsCompression" to "0.2.0",
    "expressionParser" to "1.0.29",

    // Kotlin
    "kotlinxDatetime" to "0.4.0",
    "coroutines" to "1.6.4",

    // test dependencies
    "coreTesting" to "2.1.0",
    "jUnit" to "4.13.2",
    "mockito" to "3.4.6",
    "mockitoKotlin" to "2.2.0",
    "truth" to "1.1.2",
    "testRunner" to "1.4.0",
    "equalsVerifier" to "3.4.1",
    "flipper" to "0.83.0",
    "soloader" to "0.10.1",
    "liveDataTesting" to "1.2.0",
    "commonsLogging" to "1.2",

    // open id
    "appauth" to "0.8.1"
)

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
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${libraries["libraryDesugaring"]}")

    // RxJava
    api("io.reactivex.rxjava2:rxjava:${libraries["rxJava"]}")
    api("io.reactivex.rxjava2:rxandroid:${libraries["rxAndroid"]}")

    // AndroidX
    api("androidx.annotation:annotation:${libraries["annotation"]}")
    api("androidx.paging:paging-runtime:${libraries["paging"]}")

    // Auto Value
    api("com.google.auto.value:auto-value-annotations:${libraries["autoValue"]}")
    kapt("com.google.auto.value:auto-value:${libraries["autoValue"]}")

    // Dagger
    api("com.google.dagger:dagger:${libraries["dagger"]}")
    kapt("com.google.dagger:dagger-compiler:${libraries["dagger"]}")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind:${libraries["jackson"]}")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:${libraries["jackson"]}")

    // Square libraries
    api("com.squareup.okhttp3:okhttp:${libraries["okHttp"]}")
    api("com.squareup.retrofit2:retrofit:${libraries["retrofit"]}")
    api("com.squareup.retrofit2:converter-jackson:${libraries["retrofit"]}")
    api("com.squareup.retrofit2:adapter-rxjava2:${libraries["retrofit"]}")

    // Kotlin
    api("org.jetbrains.kotlinx:kotlinx-datetime:${libraries["kotlinxDatetime"]}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libraries["coroutines"]}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${libraries["coroutines"]}")

    // sms compression library
    api("com.github.dhis2:sms-compression:${libraries["smsCompression"]}")

    // DHIS 2 antlr expression parser
    api("org.hisp.dhis.parser:dhis-antlr-expression-parser:${libraries["expressionParser"]}")

    // Extension which generates mappers for work with cursor and content values
    api("com.gabrielittner.auto.value:auto-value-cursor-annotations:${libraries["autoValueCursor"]}")
    kapt("com.gabrielittner.auto.value:auto-value-cursor:${libraries["autoValueCursor"]}")

    api("net.zetetic:android-database-sqlcipher:${libraries["sqlCipher"]}")

    api("com.squareup.okhttp3:mockwebserver:${libraries["okHttp"]}")

    // Java test dependencies
    testImplementation("junit:junit:${libraries["jUnit"]}")
    testImplementation("org.mockito:mockito-core:${libraries["mockito"]}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${libraries["mockitoKotlin"]}")
    testImplementation("com.google.truth:truth:${libraries["truth"]}") {
        exclude(group = "junit") // Android has JUnit built in.
    }
    testImplementation("nl.jqno.equalsverifier:equalsverifier:${libraries["equalsVerifier"]}")
    testImplementation("com.squareup.okhttp3:mockwebserver:${libraries["okHttp"]}")
    testImplementation("androidx.test:runner:${libraries["testRunner"]}")
    testImplementation("commons-logging:commons-logging:${libraries["commonsLogging"]}")

    // Android test dependencies
    androidTestImplementation("commons-logging:commons-logging:${libraries["commonsLogging"]}")
    androidTestImplementation("org.mockito:mockito-core:${libraries["mockito"]}")
    androidTestImplementation("com.jraska.livedata:testing-ktx:${libraries["liveDataTesting"]}")
    androidTestImplementation("androidx.arch.core:core-testing:${libraries["coreTesting"]}")
    androidTestImplementation("androidx.test:runner:${libraries["testRunner"]}")
    androidTestImplementation("androidx.test:rules:${libraries["testRunner"]}")
    androidTestImplementation("com.squareup.okhttp3:logging-interceptor:${libraries["okHttp"]}")
    androidTestImplementation("com.google.truth:truth:${libraries["truth"]}") {
        exclude(group = "junit") // Android has JUnit built in.
    }
    debugImplementation("com.facebook.flipper:flipper:${libraries["flipper"]}")
    debugImplementation("com.facebook.soloader:soloader:${libraries["soloader"]}")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:${libraries["flipper"]}") {
        exclude(group = "com.squareup.okhttp3")
    }

    releaseImplementation("com.facebook.flipper:flipper-noop:${libraries["flipper"]}")

    implementation("net.openid:appauth:${libraries["appauth"]}")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
}

detekt {
    toolVersion = "1.18.0"
    config = files("config/detekt.yml")
    parallel = true
    buildUponDefaultConfig = false
}
