package com.swyp.haruup.core.util

/**
 * 닉네임 로컬 유효성 검사. iOS 의 validateNicknameLocally 와 같은 규칙입니다.
 *
 * 서버에 중복 확인을 보내기 전에 앱에서 먼저 거릅니다.
 */
object NicknameValidator {

    private const val MIN_LENGTH = 2
    private const val MAX_LENGTH = 10

    /** 완성형 한글 음절 범위 (가 ~ 힣) */
    private val COMPLETE_SYLLABLE = '가'..'힣'

    /** 한글만 허용합니다. 완성형 음절, 호환 자모, 공백까지만 통과시킵니다. */
    private val KOREAN_ONLY = Regex("^[가-힣ㄱ-ㅎㅏ-ㅣ\\s]*$")

    /**
     * @return 통과하면 null, 통과하지 못하면 사용자에게 보여줄 안내 문구
     */
    fun validate(nickname: String): String? {
        val trimmed = nickname.trim()

        return when {
            trimmed.length < MIN_LENGTH -> "닉네임은 최소 ${MIN_LENGTH}글자 이상이어야 해요."
            trimmed.length > MAX_LENGTH -> "닉네임은 최대 ${MAX_LENGTH}글자까지 가능해요."
            !KOREAN_ONLY.matches(trimmed) -> "한글만 입력이 가능해요.\n다시 입력해주세요."
            !isCompleteKorean(trimmed) ->
                "자음이나 모음만으로는 닉네임을 만들 수 없어요.\n완성된 한글로 입력해주세요."

            else -> null
        }
    }

    /** 공백을 뺀 모든 글자가 완성형 한글 음절인지 확인합니다. */
    private fun isCompleteKorean(text: String): Boolean =
        text.filterNot { it.isWhitespace() }.all { it in COMPLETE_SYLLABLE }
}
