import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// local.properties guarda la URL del API y la llave de Google Maps: no van al
// control de versiones.
val localProperties = Properties().apply {
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

fun propiedadLocal(clave: String, porDefecto: String): String =
    (localProperties.getProperty(clave) ?: System.getenv(clave) ?: porDefecto)

android {
    namespace = "com.sipinna.atizapan.reportes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sipinna.atizapan.reportes"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = propiedadLocal("MAPS_API_KEY", "")

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${propiedadLocal("API_BASE_URL", "https://localhost/")}\"",
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// El jvmTarget de Kotlin se configura en la extension de nivel de proyecto:
// dentro del bloque android {} el nombre "kotlin" no resuelve.
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.core.ktx)

    // --- Compose + Material 3 --------------------------------------------
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    debugImplementation(libs.compose.ui.tooling)

    // --- Red ---------------------------------------------------------------
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)

    // --- Mapas y ubicacion -------------------------------------------------
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    // --- Imagenes y camara -------------------------------------------------
    implementation(libs.coil.compose)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // --- Inyeccion de dependencias -----------------------------------------
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // --- Persistencia y concurrencia ---------------------------------------
    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play.services)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
