import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    kotlin(KotlinPlugins.serialization).version(Kotlin.version)
    id(Plugins.googleServices)
    id(Plugins.sqlDelight)
}

kotlin {
    
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            binaryOption("bundleId", "app.israelori.am")
            export(KMPNotifier.main)
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(SQLDelight.androidDriver)
            implementation(Koin.android)
            implementation(Compose.icons)
            implementation(Firebase.messaging)
            implementation(AndroidX.startup)
            implementation(libs.accompanist.permissions)
            implementation(Ktor.android)
        }

        iosMain.dependencies {
            implementation(SQLDelight.nativeDriver)
            implementation(Ktor.darwin)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation(Firebase.firestore)
            implementation(Firebase.database)
            implementation(Firebase.functions)
            implementation(Firebase.auth)
            implementation(Firebase.common)
            implementation(Firebase.storage)
            implementation(Kotlinx.serialization)
            implementation(SQLDelight.runtime)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(project.dependencies.platform(Koin.bom))
            implementation(Koin.core)
            implementation(Koin.compose)
            implementation(Touchlab.common)
            implementation(Voyager.tabNavigator)
            implementation(Voyager.navigator)
            implementation(Voyager.transitions)
            implementation(Voyager.bottomSheet)
            api(KMPNotifier.main)
            implementation(Datetime.main)
            implementation(Coil3.core)
            implementation(Coil3.compose)
            implementation(Coil3.network)
            implementation(Ktor.core)
            implementation(libs.peekaboo.ui)
            implementation(libs.peekaboo.image.picker)

        }
    }
}

android {
    namespace = "app.business.manager"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "app.business.manager"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
        implementation(platform(Firebase.bom))
    }
}

dependencies {
    implementation(libs.firebase.common.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.animation.android)
}

sqldelight {
    databases {
        create("UserDatabase") {
            packageName.set("app.business.manager.database")
        }
    }
}