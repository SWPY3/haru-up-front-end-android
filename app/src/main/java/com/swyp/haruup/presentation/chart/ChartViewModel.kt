package com.swyp.haruup.presentation.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.model.ChartItem
import com.swyp.haruup.network.service.ChartService
import com.swyp.haruup.network.service.JobService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    /** 서버에서 받아온 세부 직무 이름 → id. 비어 있으면 시트에서 세부 직무 섹션을 감춥니다. */
    val jobDetails: JobDetailIds = emptyMap(),
) {
    val filterSections: List<ChartFilterSection> = ChartFilter.sections(jobDetails)

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
    private val jobService: JobService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    init {
        fetchRanking(emptyList())
        loadJobDetails()
    }

    /**
     * 직업마다 세부 직무를 받아 이름별 id 로 묶어 둡니다.
     *
     * 하나라도 못 받으면 섹션을 통째로 감춥니다.
     * 일부만 보이면 그 직업의 세부 직무는 고를 수 없는데 이유를 알 수 없기 때문입니다.
     */
    private fun loadJobDetails() {
        viewModelScope.launch {
            // coroutineScope 로 묶어야 한 요청의 실패가 runCatching 에서 잡힙니다.
            // 묶지 않으면 실패가 바깥 launch 로 퍼져 앱이 종료됩니다.
            val details = runCatching {
                coroutineScope {
                    ChartFilter.JOB_ID.values
                        .map { jobId -> async { jobService.jobDetails(jobId) } }
                        .awaitAll()
                        .flatten()
                }
            }.getOrNull() ?: return@launch

            _uiState.update { it.copy(jobDetails = ChartFilter.groupJobDetails(details)) }
        }
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
        val query = ChartFilter.toQuery(tags, _uiState.value.jobDetails)

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
