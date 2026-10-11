package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.CharacterData
import com.swyp.haruup.data.model.PersonalityData
import com.swyp.haruup.data.model.SelectPersonalityRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * iOS 의 CharacterService 에 대응합니다.
 */
interface CharacterService {

    /** 이 API 만 ApiResponse 래퍼 없이 배열을 그대로 반환합니다. */
    @GET(ApiPath.Character.LIST)
    suspend fun characterList(): List<CharacterData>

    @GET(ApiPath.Character.PERSONALITY_LIST)
    suspend fun personalityList(): ApiResponse<List<PersonalityData>>

    @POST(ApiPath.Character.SELECT_PERSONALITY)
    suspend fun selectPersonality(@Body request: SelectPersonalityRequest): ApiResponse<String>
}
