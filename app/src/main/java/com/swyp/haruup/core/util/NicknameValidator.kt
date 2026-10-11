package com.swyp.haruup.core.util

/**
 * 닉네임이 규칙에 어긋난 이유입니다.
 *
 * 안내 문구는 화면마다 다릅니다. (큐레이션은 길게, 프로필 수정은 한 줄로)
 * 그래서 검사는 이유만 돌려주고 문구는 각 화면이 정합니다.
 */
enum class NicknameValidation {
    VALID,
    TOO_SHORT,
    TOO_LONG,
    NOT_KOREAN,
    INCOMPLETE_KOREAN,
}

/**
 * 닉네임 로컬 유효성 검사. iOS 의 validateNickname 과 같은 규칙입니다.
 *
 * 서버에 중복 확인을 보내기 전에 앱에서 먼저 거릅니다.
 */
object NicknameValidator {

    const val MIN_LENGTH = 2
    const val MAX_LENGTH = 10

    /** 완성형 한글 음절 범위 (가 ~ 힣) */
    private val COMPLETE_SYLLABLE = '가'..'힣'

    /** 한글만 허용합니다. 완성형 음절, 호환 자모, 공백까지만 통과시킵니다. */
    private val KOREAN_ONLY = Regex("^[가-힣ㄱ-ㅎㅏ-ㅣ\\s]*$")

    /** 검사 순서도 iOS 와 같습니다. 길이 → 한글만 → 완성형. */
    fun check(nickname: String): NicknameValidation {
        val trimmed = nickname.trim()

        return when {
            trimmed.length < MIN_LENGTH -> NicknameValidation.TOO_SHORT
            trimmed.length > MAX_LENGTH -> NicknameValidation.TOO_LONG
            !KOREAN_ONLY.matches(trimmed) -> NicknameValidation.NOT_KOREAN
            !isCompleteKorean(trimmed) -> NicknameValidation.INCOMPLETE_KOREAN
            else -> NicknameValidation.VALID
        }
    }

    /**
     * 큐레이션 챗봇에서 쓰는 안내 문구입니다.
     *
     * @return 통과하면 null, 통과하지 못하면 사용자에게 보여줄 안내 문구
     */
    fun validate(nickname: String): String? = when (check(nickname)) {
        NicknameValidation.TOO_SHORT -> "닉네임은 최소 ${MIN_LENGTH}글자 이상이어야 해요."
        NicknameValidation.TOO_LONG -> "닉네임은 최대 ${MAX_LENGTH}글자까지 가능해요."
        NicknameValidation.NOT_KOREAN -> "한글만 입력이 가능해요.\n다시 입력해주세요."
        NicknameValidation.INCOMPLETE_KOREAN ->
            "자음이나 모음만으로는 닉네임을 만들 수 없어요.\n완성된 한글로 입력해주세요."

        NicknameValidation.VALID -> null
    }

    /** 공백을 뺀 모든 글자가 완성형 한글 음절인지 확인합니다. */
    private fun isCompleteKorean(text: String): Boolean =
        text.filterNot { it.isWhitespace() }.all { it in COMPLETE_SYLLABLE }
}
