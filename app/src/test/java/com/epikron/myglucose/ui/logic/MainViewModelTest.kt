@file:OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)

package com.epikron.myglucose.ui.logic

import android.content.SharedPreferences
import com.epikron.myglucose.data.local.GlycemicLevelDao
import com.epikron.myglucose.data.model.GlycemicLevelModel
import com.epikron.myglucose.data.model.NICE_DATE
import com.epikron.myglucose.data.repository.DataRepository
import com.epikron.myglucose.ui.model.DisplayGlycemicLevel
import com.epikron.myglucose.ui.model.MainScreenState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private val mockDao: GlycemicLevelDao = mockk()
    private val mockDataRepository: DataRepository = DataRepository(mockDao)
    private val mockSharedPreferences: SharedPreferences = mockk()
    private val mockSharedPreferencesEditor: SharedPreferences.Editor = mockk()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        coEvery { mockDao.getAllEntries() } returns emptyList()
        coEvery { mockDao.insertEntry(any()) } just (runs)
        coEvery { mockSharedPreferences.getBoolean(any(), any()) } returns true
        coEvery { mockSharedPreferencesEditor.putBoolean(any(), any()) } returns mockSharedPreferencesEditor
        coEvery { mockSharedPreferencesEditor.apply() } returns Unit
        coEvery { mockSharedPreferences.edit() } returns mockSharedPreferencesEditor
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `when data from repository requested returns the test list`() {
        coEvery { mockDao.getAllEntries() } returns GlycemicLevelModel.testList
        runTest {
            val result = mockDataRepository.getEntries()
            coVerify { mockDao.getAllEntries() }
            assertTrue(result == GlycemicLevelModel.testList)
        }
    }

    @Test
    fun `when viewModel data is saved an entry is added to the database`() {
        runTest {
            val mockViewModel = MainViewModel(mockDataRepository, mockSharedPreferences)
            mockViewModel.onTextInput("1234")
            mockViewModel.onSaveClicked()
            coVerify { mockDataRepository.addEntry(any()) }
            coVerify { mockDao.insertEntry(any()) }
        }
    }

    @Test
    fun `when viewModel data is saved the new list contains the data and the average is correct`() {
        runTest {
            val mockViewModel = MainViewModel(mockDataRepository, mockSharedPreferences)
            launch { mockViewModel.onTextInput("1234") }
            launch { mockViewModel.onSaveClicked() }
            launch { mockViewModel.onTextInput("4321") }
            launch { mockViewModel.onSaveClicked() }
            advanceUntilIdle()
            coVerify { mockSharedPreferences.getBoolean(MainViewModel.UNITS_KEY, true) }
            coVerify { mockDataRepository.getEntries() }
            coVerify(exactly = 2) { mockDataRepository.addEntry(any()) }
            val average = ((1234.0 + 4321.0) / 2).closestInt().toString()
            val currentUiState = mockViewModel.uiState.value
            val displayListItems = listOf(
                DisplayGlycemicLevel(NICE_DATE, "4321"),
                DisplayGlycemicLevel(NICE_DATE, "1234")
            )
            val expectedUiState = MainScreenState(average, true, "", false, displayListItems)
            assertTrue(currentUiState.averageBGMg == expectedUiState.averageBGMg)
            assertTrue(currentUiState.isUnitMg == expectedUiState.isUnitMg)
            assertTrue(currentUiState.history.size == displayListItems.size)
            assertTrue(currentUiState.history[0].value == displayListItems[0].value)
            assertTrue(currentUiState.history[1].value == displayListItems[1].value)
        }
    }

    @Test
    fun `when viewModel units are changed the units are correct and the average is correct`() {
        runTest {
            val mockViewModel = MainViewModel(mockDataRepository, mockSharedPreferences)
            val collectJob = launch { mockViewModel.uiState.collect {} }
            launch { mockViewModel.onTextInput("18") }
            launch { mockViewModel.onSaveClicked() }
            launch { mockViewModel.onUnitChange(false) }
            advanceUntilIdle()
            coVerify { mockSharedPreferences.getBoolean(MainViewModel.UNITS_KEY, true) }
            coVerify { mockDataRepository.getEntries() }
            coVerify { mockDataRepository.addEntry(any()) }
            val currentUiState = mockViewModel.uiState.value
            val expectedUiState = MainScreenState("1", false, "", false, listOf(DisplayGlycemicLevel.test))
            assertTrue(currentUiState.averageBGMg == expectedUiState.averageBGMg)
            assertTrue(currentUiState.isUnitMg == expectedUiState.isUnitMg)
            collectJob.cancel()
        }
    }

    @Test
    fun `when a 0 value is given no value should be saved and an error should be displayed`() {
        runTest {
            val mockViewModel = MainViewModel(mockDataRepository, mockSharedPreferences)
            val collectJob = launch { mockViewModel.uiState.collect {} }
            launch { mockViewModel.onTextInput("0") }
            launch { mockViewModel.onSaveClicked() }
            advanceUntilIdle()
            coVerify { mockSharedPreferences.getBoolean(MainViewModel.UNITS_KEY, true) }
            coVerify { mockDataRepository.getEntries() }
            coVerify(exactly = 0) { mockDataRepository.addEntry(any()) }
            val currentUiState = mockViewModel.uiState.value
            assertTrue(currentUiState.inputError)
            collectJob.cancel()
        }
    }
}
