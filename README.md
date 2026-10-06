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

**1. 소셜 로그인 앱 키 넣기** — 코드는 다 붙어 있고 키만 넣으면 됩니다.

`local.properties` 에 아래 세 줄을 추가하세요. 이 파일은 gitignore 처리돼 있어 커밋되지 않습니다.

```properties
KAKAO_NATIVE_APP_KEY=발급받은_네이티브_앱_키
NAVER_CLIENT_ID=발급받은_클라이언트_ID
NAVER_CLIENT_SECRET=발급받은_클라이언트_시크릿
```

넣고 다시 빌드하면 해당 로그인이 바로 동작합니다. 그 외에 손댈 곳은 없습니다.
카카오 저장소, `AuthCodeHandlerActivity`, SDK 초기화, 리다이렉트 scheme 은 모두 설정돼 있습니다.

키가 없으면 앱은 그대로 돌아가고, 그 버튼만 "아직 준비되지 않았어요" 로 안내합니다.
빈 키로 SDK 를 초기화하면 앱 전체가 뜨지 않아 초기화 자체를 건너뜁니다.

**아직 남은 SDK**

| SDK | 필요한 작업 |
|-----|------------|
| Firebase FCM | `google-services.json` 배치(gitignore 처리됨), 플러그인 추가 |
| Amplitude | API 키 발급 |
| AdMob | 앱 ID 발급 (마이페이지 배너) |

**2. 디자인 리소스 이관** — ✅ 완료

| iOS | Android | 비고 |
|-----|---------|------|
| `Assets.xcassets/color` (47종) | `core/designsystem/Color.kt` | 에셋명을 그대로 사용 |
| `Fonts/Pretendard` (9종) | `res/font/pretendard_*.ttf` | |
| `Fonts/Typography.swift` (25종) | `core/designsystem/Type.kt` | `HaruUpType.body1` 형태로 이름 일치 |
| `Animations/*.json` (4종) | `res/raw/` | `loadingCircle` → `loading_circle` |
| `AppIcon.appiconset` | `res/mipmap-*/` | 레거시 + Adaptive 아이콘 생성 |

화면 이관 시 iOS 에셋명으로 검색하면 대응되는 Kotlin 상수를 찾을 수 있습니다.

아직 옮기지 않은 이미지 에셋(캐릭터, 아이콘, 버튼 등)은 화면을 구현하면서 필요한 것부터 추가합니다.

**3. 화면 구현 순서 (권장)**
로그인 → 큐레이션 → 홈 → 기록/차트 → 마이페이지

**4. 미이관 API**
`network/ApiPath.kt` 에 Auth / Chatbot / Character 만 옮겨져 있습니다.
Mission / Member / Chart / Interests / Job / Notification / Ad 는 iOS `NetworkDefine.swift` 를 참고해 추가하세요.
