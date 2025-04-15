package com.aliernfrog.ensimanager.ui.component.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RadioButtons(
    choices: List<RadioButtonChoice>,
    selectedOptionIndex: Int,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    onSelect: (Int) -> Unit
) {
    choices.forEachIndexed { index, choice ->
        val selected = selectedOptionIndex == index
        val onSelected = {
            onSelect(choice.indexOverride ?: index)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelected() }
                .padding(horizontal = 2.dp)
        ) {
            RadioButton(
                selected = selected,
                onClick = { onSelected() },
                colors = colors,
                enabled = choice.enabled
            )
            FormHeader(
                title = choice.title,
                description = choice.description,
                modifier = Modifier.alpha(
                    if (choice.enabled) 1f else 0.7f
                )
            )
            Text(
                text = choice.title,
                color = contentColor
            )
        }
    }
}

data class RadioButtonChoice(
    val title: String,
    val description: String? = null,
    val enabled: Boolean = true,
    val indexOverride: Int? = null
)