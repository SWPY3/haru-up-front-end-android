import java.util.Properties

/**
 * SNS 앱 키는 저장소에 올리지 않습니다. local.properties 에서 읽고, 없으면 빈 값으로 둡니다.
 * 빈 값이면 앱은 그대로 돌아가고 해당 로그인 버튼만 "준비되지 않았어요" 로 안내합니다.
 */
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun secret(name: String): String = localProperties.getProperty(name) ?: ""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

/**
 * Firebase 설정 파일은 저장소에 올리지 않습니다. (gitignore)
 * 파일이 있을 때만 플러그인을 붙입니다. 없으면 빌드는 되고 푸시만 꺼집니다.
 */
if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
}

android {
    namespace = "com.swyp.haruup"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.swyp.haruup"
        minSdk = 26
        targetSdk = 36

        // iOS 와 버전을 맞춥니다.
        versionCode = 1
        versionName = "1.0.11"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${secret("KAKAO_NATIVE_APP_KEY")}\"")
        buildConfigField("String", "NAVER_CLIENT_ID", "\"${secret("NAVER_CLIENT_ID")}\"")
        buildConfigField("String", "NAVER_CLIENT_SECRET", "\"${secret("NAVER_CLIENT_SECRET")}\"")

        buildConfigField("String", "AMPLITUDE_API_KEY", "\"${secret("AMPLITUDE_API_KEY")}\"")

        // 키가 없으면 Google 이 공개한 테스트 광고를 씁니다. 실제 광고는 local.properties 에 넣어야 나옵니다.
        // 앱 ID 가 비어 있으면 광고 SDK 가 앱 실행 즉시 종료시키므로 테스트 ID 로라도 채워 둡니다.
        manifestPlaceholders["admobAppId"] =
            secret("ADMOB_APP_ID").ifBlank { "ca-app-pub-3940256099942544~3347511713" }
        buildConfigField(
            "String",
            "ADMOB_BANNER_UNIT_ID",
            "\"${secret("ADMOB_BANNER_UNIT_ID").ifBlank { "ca-app-pub-3940256099942544/9214589741" }}\"",
        )

        // 카카오톡으로 로그인한 뒤 앱으로 돌아올 때 쓰는 주소입니다. Manifest 의 intent-filter 에 들어갑니다.
        manifestPlaceholders["kakaoRedirectScheme"] = "kakao${secret("KAKAO_NATIVE_APP_KEY")}"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            buildConfigField("String", "BASE_URL", "\"https://haru-up-back-end-production.up.railway.app/\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://haru-up-back-end-production.up.railway.app/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
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

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // Local storage
    implementation(libs.androidx.datastore.preferences)

    // Animation
    implementation(libs.lottie.compose)

    // 약관 원문을 Custom Tabs 로 띄운다. iOS 의 인앱 웹뷰에 대응한다.
    implementation(libs.androidx.browser)

    implementation(libs.kakao.user)
    implementation(libs.naver.oauth)

    // 분석 / 광고 / 푸시 (iOS 와 같은 SDK)
    implementation(libs.amplitude.analytics)
    implementation(libs.play.services.ads)
    implementation(libs.ump)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
