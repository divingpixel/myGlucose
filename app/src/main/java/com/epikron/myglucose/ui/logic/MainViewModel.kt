package com.epikron.myglucose.ui.logic

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epikron.myglucose.data.model.GlycemicLevelModel
import com.epikron.myglucose.data.model.GlycemicLevelModel.Companion.toDisplay
import com.epikron.myglucose.data.repository.DataRepository
import com.epikron.myglucose.ui.model.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private var inputError: Boolean = false
    private var inputText: MutableStateFlow<String> = MutableStateFlow("")
    private var isUnitMg: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private var historyList: MutableStateFlow<List<GlycemicLevelModel>> = MutableStateFlow(listOf())
    private var averageLevel: MutableStateFlow<Double> = MutableStateFlow(0.0)

    val uiState: StateFlow<MainScreenState> = combine(inputText, isUnitMg, historyList, averageLevel) { text, isMg, history, average ->
        MainScreenState(
            averageBGMg = (if (isUnitMg.value) average else average.toMmol()).closestInt().toString(),
            isUnitMg = isMg,
            textInput = text,
            inputError = inputError,
            history = history.map { it.toDisplay(isMg) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainScreenState.empty
    )

    init {
        viewModelScope.launch {
            historyList.value = dataRepository.getEntries()
            isUnitMg.value = sharedPreferences.getBoolean(UNITS_KEY, true)
        }
    }

    fun onUnitChange(isMg: Boolean) {
        isUnitMg.value = isMg
        sharedPreferences.edit(false) {
            putBoolean(UNITS_KEY, isMg)
        }
    }

    fun onTextInput(text: String) {
        inputError = false
        inputText.value = text
    }

    fun onSaveClicked() {
        val input = inputText.value.toDouble()
        if (input > 0) {
            val updatedList: MutableList<GlycemicLevelModel> = historyList.value.toMutableList()
            val newGlycemicLevelEntry = GlycemicLevelModel(
                mg = if (isUnitMg.value) input else input.toMg(),
                timestamp = System.currentTimeMillis(),
                localTime = System.currentTimeMillis().toNiceTime()
            )
            viewModelScope.launch {
                dataRepository.addEntry(newGlycemicLevelEntry)
            }
            updatedList.add(0, newGlycemicLevelEntry)
            averageLevel.value = updatedList.sumOf { it.mg } / updatedList.size
            historyList.value = updatedList
        } else {
            inputError = true
        }
        inputText.value = ""
    }

    companion object {
        const val UNITS_KEY = "unitIsMg"
    }
}
