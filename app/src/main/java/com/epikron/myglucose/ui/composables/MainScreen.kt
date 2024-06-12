package com.epikron.myglucose.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.epikron.myglucose.R
import com.epikron.myglucose.ui.logic.numbersPattern
import com.epikron.myglucose.ui.model.DisplayGlycemicLevel
import com.epikron.myglucose.ui.model.MainScreenState
import com.epikron.myglucose.ui.theme.MyGlucoseTheme
import com.epikron.myglucose.ui.theme.Space

@Composable
fun GlycemicLevelListItem(item: DisplayGlycemicLevel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(modifier = Modifier.padding(start = Space.large), text = item.date)
        Text(modifier = Modifier.padding(end = Space.large), text = item.value)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.HistoryList(items: List<DisplayGlycemicLevel>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalDivider(modifier = Modifier.padding(Space.tiny), thickness = Space.tiny)
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier.padding(start = Space.large),
                        text = stringResource(id = R.string.date),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        modifier = Modifier.padding(end = Space.large),
                        text = stringResource(id = R.string.measurement),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            items(items) { item ->
                GlycemicLevelListItem(item = item)
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier,
    uiState: MainScreenState,
    onUnitChange: (Boolean) -> Unit,
    onTextInput: (String) -> Unit,
    onSaveClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(Space.medium)
            .fillMaxSize()
    ) {
        val unit = stringResource(id = if (uiState.isUnitMg) R.string.unit_mg else R.string.unit_mmol)
        Text(
            modifier = Modifier.padding(Space.none, Space.small),
            text = stringResource(id = R.string.average, uiState.averageBGMg, unit),
            style = MaterialTheme.typography.bodyLarge
        )
        HorizontalDivider(modifier = Modifier.padding(Space.tiny), thickness = Space.tiny)
        Text(
            modifier = Modifier.padding(Space.none, Space.small),
            text = stringResource(id = R.string.add),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = uiState.isUnitMg,
                onClick = { onUnitChange(true) },
                enabled = true,
                colors = RadioButtonDefaults.colors().copy(selectedColor = MaterialTheme.colorScheme.tertiary)
            )
            Text(
                text = stringResource(id = R.string.unit_mg),
                modifier = Modifier.padding(start = Space.small)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !uiState.isUnitMg,
                onClick = { onUnitChange(false) },
                enabled = true,
                colors = RadioButtonDefaults.colors().copy(selectedColor = MaterialTheme.colorScheme.tertiary)
            )
            Text(
                text = stringResource(id = R.string.unit_mmol),
                modifier = Modifier.padding(start = Space.small)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.textInput,
                onValueChange = {
                    if (it.isEmpty() || it.matches(numbersPattern)) onTextInput(it)
                },
                singleLine = true,
                modifier = Modifier
                    .padding(Space.medium, Space.small)
                    .weight(3f, true),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    cursorColor = MaterialTheme.colorScheme.tertiary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiary
                ),
                isError = uiState.inputError,
                supportingText = {
                    if (uiState.inputError) {
                        Text(
                            text = stringResource(id = R.string.input_error),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Text(
                text = unit,
                modifier = Modifier
                    .padding(start = Space.small)
                    .weight(1f, false)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Space.small),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.padding(end = Space.medium, bottom = Space.medium),
                onClick = onSaveClicked
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = Space.medium),
                    text = stringResource(id = R.string.save),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        if (uiState.history.isNotEmpty()) {
            HistoryList(items = uiState.history)
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    MyGlucoseTheme {
        Scaffold {
            MainScreen(Modifier.padding(it), MainScreenState.test, {}, {}, {})
        }
    }
}
