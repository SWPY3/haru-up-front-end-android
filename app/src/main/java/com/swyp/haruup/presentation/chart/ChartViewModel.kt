package com.swyp.haruup.presentation.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.model.ChartItem
import com.swyp.haruup.network.service.ChartService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChartUiState(
    val items: List<ChartItem> = emptyList(),
    /** 필터 바에 칩으로 보이는, 사용자가 고른 한글 태그 */
    val selectedTags: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isTooltipVisible: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
) {
    val hasData: Boolean = items.isNotEmpty()

    /** 태그가 하나라도 있으면 필터 바가 칩 목록 + 초기화 버튼 모양으로 바뀝니다. */
    val isFilterActive: Boolean = selectedTags.isNotEmpty()
}

/**
 * 메인 탭의 차트. iOS 의 ChartViewModel 에 대응합니다.
 */
@HiltViewModel
class ChartViewModel @Inject constructor(
    private val chartService: ChartService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    init {
        fetchRanking(emptyList())
    }

    fun onInfoClick() {
        _uiState.update { it.copy(isTooltipVisible = !it.isTooltipVisible) }
    }

    fun onTooltipDismiss() {
        _uiState.update { it.copy(isTooltipVisible = false) }
    }

    fun onFilterClick() {
        _uiState.update { it.copy(isFilterSheetVisible = true) }
    }

    fun onFilterSheetDismiss() {
        _uiState.update { it.copy(isFilterSheetVisible = false) }
    }

    /** 바텀시트에서 "결과 보기" 를 눌렀을 때입니다. */
    fun onFilterApplied(tags: List<String>) {
        _uiState.update { it.copy(selectedTags = tags, isFilterSheetVisible = false) }
        fetchRanking(tags)
    }

    /** 칩의 X 를 눌러 조건 하나를 뺍니다. 남은 조건으로 다시 조회합니다. */
    fun onTagRemoved(tag: String) {
        val remaining = _uiState.value.selectedTags - tag
        _uiState.update { it.copy(selectedTags = remaining) }
        fetchRanking(remaining)
    }

    /** 초기화 버튼. 조건을 모두 지우고 전체 차트를 다시 받아옵니다. */
    fun onResetClick() {
        _uiState.update { it.copy(selectedTags = emptyList()) }
        fetchRanking(emptyList())
    }

    /**
     * 실패해도 이미 받아 둔 차트는 지우지 않습니다.
     * 조건을 바꾸다 통신이 한 번 실패했다고 화면이 비어 버리면 더 혼란스럽기 때문입니다. (iOS 와 같은 정책)
     */
    private fun fetchRanking(tags: List<String>) {
        val query = ChartFilter.toQuery(tags)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val items = runCatching {
                chartService.popularRanking(
                    limit = query.limit,
                    gender = query.gender,
                    ageGroups = query.ageGroups.ifEmpty { null },
                    jobIds = query.jobIds.ifEmpty { null },
                    jobDetailIds = query.jobDetailIds.ifEmpty { null },
                    interests = query.interests.ifEmpty { null },
                )
            }.getOrNull()
                ?.takeIf { it.success }
                ?.data

            _uiState.update { state ->
                state.copy(items = items ?: state.items, isLoading = false)
            }
        }
    }
}
