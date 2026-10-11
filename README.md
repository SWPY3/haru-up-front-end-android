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

홈에서 미션을 고르거나 더하면 소개 화면(하루 한 번) → 미션 목록이 탭바 위에 열립니다. (iOS 의 모달과 같은 흐름)

### 구현 현황

| 영역 | 상태 |
|------|------|
| 스플래시 / 로그인 / 약관 / 온보딩 | ✅ UI + API |
| 큐레이션 (캐릭터 → 성격 → 챗봇 → 오늘의 미션) | ✅ UI + API (챗봇 완료 시 캐릭터·닉네임 저장) |
| 홈 (상단, 오늘의 미션, 미션 고르기·더하기, 바텀시트) | ✅ UI + API |
| 기록 (캘린더, 날짜별 미션, 성장 차트) | ✅ UI + API |
| 차트 (인기 미션, 필터) | ✅ UI + API (필터 값은 서버 기준) |
| 마이페이지 (프로필·관심사·알림 설정, 로그아웃, 탈퇴) | ✅ UI + API |
| 인증 | ✅ 만료 시 자동 재발급 (`jwt-token-login`), 재발급 실패 시 로그인으로 이동 |

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

**분석 · 광고 · 푸시 SDK** — 코드는 iOS 와 같게 붙어 있고, 키나 파일이 없으면 그 기능만 꺼진 채로 앱이 돕니다.

| SDK | 넣을 것 | 없을 때 |
|-----|--------|--------|
| Amplitude | `local.properties` 에 `AMPLITUDE_API_KEY` (iOS 와 같은 프로젝트 키를 써도 됩니다) | 이벤트를 보내지 않음 |
| AdMob | `local.properties` 에 `ADMOB_APP_ID`, `ADMOB_BANNER_UNIT_ID` (**Android 앱용으로 따로 발급**, iOS ID 는 쓸 수 없음) | Google 테스트 광고가 나옴 |
| Firebase FCM | Firebase 콘솔에서 Android 앱(`com.swyp.haruup`)을 등록하고 `app/google-services.json` 배치 | 푸시 토큰을 등록하지 않음 |

- 이벤트 이름은 iOS `AnalyticsManager` 와 글자까지 같습니다. (`AnalyticsTest` 가 확인)
- 광고 동의(UMP)는 iOS 처럼 로그인 화면에서 받고, 배너는 홈 목록 맨 아래와 마이페이지 메뉴 아래에 있습니다.
- 푸시 토큰은 로그인 직후와 토큰이 바뀔 때 `api/notifications/token` 으로 보냅니다. 알림 data 의 `type` 이 `home` 이면 홈 탭으로 갑니다.

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

**3. 차트 필터** — 서버(`haru-up-back-end-2`) 값에 맞췄습니다. iOS 는 아직 예전 값을 씁니다.

| 필터 | 내용 |
|------|------|
| 연령대 | 서버 `AgeGroup` 구간(10대, 20~22세 … 50대 이상)을 그대로 칩으로 씁니다. |
| 세부 직무 | `/api/job/getJobDetailList` 로 직업마다 받아 칩으로 보여 줍니다. 못 받아오면 섹션을 감춥니다. |
| 관심사 | 서버 대분류 이름("재테크 및 투자" 등)과 같게 맞췄습니다. |

**4. 미이관 API**
`network/ApiPath.kt` 기준으로 Notification(FCM 토큰 등록) / Ad 가 남았습니다.
