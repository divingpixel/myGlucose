package com.epikron.myglucose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.epikron.myglucose.ui.composables.MainScreen
import com.epikron.myglucose.ui.logic.MainViewModel
import com.epikron.myglucose.ui.theme.MyGlucoseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGlucoseTheme {
                Scaffold {
                    MainScreen(
                        modifier = Modifier.padding(it),
                        uiState = mainViewModel.uiState.collectAsState().value,
                        onUnitChange = mainViewModel::onUnitChange,
                        onTextInput = mainViewModel::onTextInput,
                        onSaveClicked = mainViewModel::onSaveClicked
                    )
                }
            }
        }
    }
}
