# HaruUp Android

<p align="center">
  <img src="https://img.shields.io/badge/Android-26+-3DDC84?logo=android" />
  <img src="https://img.shields.io/badge/Kotlin-2.1.20-7F52FF?logo=kotlin" />
  <img src="https://img.shields.io/badge/Compose-BOM%202025.04-4285F4?logo=jetpackcompose" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-blueviolet" />
</p>

> iOS 앱([haru-up-front-end](../haru-up-front-end))의 Android 구현체입니다.
> 기술 선정 근거는 Notion "Android 개발 방식 기술 검토" 문서를 참고하세요.

---

## 🛠 기술 스택

| 구분 | 내용 | iOS 대응 |
|------|------|---------|
| **언어** | Kotlin 2.1.20 | Swift 5.0 |
| **최소 지원** | Android 8.0 (API 26) | iOS 16.4 |
| **UI** | Jetpack Compose | UIKit |
| **아키텍처** | MVVM + Navigation Compose | MVVM-C |
| **비동기** | Coroutines + Flow | RxSwift / RxCocoa |
| **DI** | Hilt | Coordinator 수동 주입 |
| **네트워킹** | Retrofit + OkHttp | Alamofire |
| **직렬화** | kotlinx.serialization | Codable |
| **로컬 저장소** | DataStore | UserDefaults / Keychain |
| **애니메이션** | Lottie Compose | Lottie |
| **빌드** | Gradle 8.14.3 (KTS + Version Catalog) | SPM |

---

## 🚀 시작하기

Android Studio 에서 이 폴더를 열면 됩니다. 터미널 빌드는 아래와 같습니다.

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
```

`local.properties` 는 로컬 SDK 경로라 커밋되지 않습니다. 새 환경에서는 Android Studio 가 자동 생성합니다.

---

## 📁 디렉토리 구조

iOS 프로젝트 구조와 1:1로 대응하도록 구성했습니다.

```
app/src/main/java/com/swyp/haruup/
├── HaruUpApplication.kt   # AppDelegate 대응 (SDK 초기화)
├── MainActivity.kt        # SceneDelegate 대응
├── core/
│   ├── designsystem/      # Color, Type, Theme
│   ├── component/         # 재사용 UI 컴포넌트
│   └── util/
├── data/
│   ├── model/             # DTO (@Serializable)
│   ├── local/             # TokenStorage (DataStore)
│   └── repository/
├── network/
│   ├── ApiResponse.kt     # GenericResponse<T> 대응
│   ├── ApiPath.kt         # NetworkDefine 대응
│   ├── interceptor/       # AuthInterceptor
│   └── service/           # Retrofit 인터페이스
├── di/                    # Hilt 모듈
└── presentation/
    ├── navigation/        # Coordinator 대응 (Route, NavHost)
    ├── splash/ login/ agree/ onboarding/ curation/
    └── maintab/ home/ history/ chart/ mypage/
```

---

## 🗺️ 화면 흐름

```
스플래시
  ├─ 로그인 미완료 → 로그인 → 약관 동의 → 온보딩 → 큐레이션(9단계) ┐
  └─ 로그인 완료 ─────────────────────────────────────────────┤
                                                              ↓
                                                    메인 탭
                                                      ├ 🏠 홈
                                                      ├ 📅 기록
                                                      ├ 📊 차트
                                                      └ 👤 마이페이지
```

현재 각 화면은 `PlaceholderScreen` 자리표시자입니다. 화면 전환 흐름만 동작합니다.

---

## ✅ 다음 단계

**1. 외부 SDK 연동** — `gradle/libs.versions.toml` 하단에 주석으로 준비해 뒀습니다.

| SDK | 추가로 필요한 작업 |
|-----|------------------|
| 카카오 로그인 | 앱 키 발급, `settings.gradle.kts` 에 카카오 저장소 추가, Manifest 에 `AuthCodeHandlerActivity` 등록 |
| 네이버 로그인 | 클라이언트 ID/Secret 발급, `NidOAuth.initialize()` 호출 |
| Firebase FCM | `google-services.json` 배치(gitignore 처리됨), 플러그인 추가 |
| Amplitude | API 키 발급 |

앱 키는 `local.properties` 또는 `secrets.properties` 에 두고 `BuildConfig` 로 주입합니다. 두 파일 모두 gitignore 처리돼 있습니다.

**2. 디자인 리소스 이관**
- `Resources/Assets.xcassets/color` → `core/designsystem/Color.kt`
- `Resources/Fonts` → `res/font` + `Type.kt`
- `Resources/Animations` (Lottie JSON) → `res/raw`
- AppIcon → `res/mipmap` (현재 임시 아이콘)

**3. 화면 구현 순서 (권장)**
로그인 → 큐레이션 → 홈 → 기록/차트 → 마이페이지

**4. 미이관 API**
`network/ApiPath.kt` 에 Auth / Chatbot / Character 만 옮겨져 있습니다.
Mission / Member / Chart / Interests / Job / Notification / Ad 는 iOS `NetworkDefine.swift` 를 참고해 추가하세요.
