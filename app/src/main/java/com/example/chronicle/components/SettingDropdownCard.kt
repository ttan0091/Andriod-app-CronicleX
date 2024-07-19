package com.example.chronicle.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.chronicle.viewmodel.NavigationViewModel

/**
 * the button/card with dropdown in setting (e.g. used for language selection)
 * **/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingDropdownCard(
    description: String,
    icon: ImageVector,
    navViewModel: NavigationViewModel,
    onOptionClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("English", "简体中文", "Français", "Deutsch", "日本語", "한국어")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { expanded = !expanded }
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 10.dp),
                imageVector = icon,
                contentDescription = description
            )
            Text(
                text = description,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd)
            ) {
                Text(
                    text = navViewModel.selectedLanguage.value,
                    modifier = Modifier.clickable { expanded = true }.padding(end = 30.dp)
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                expanded = false
                                onOptionClick(option)
                            }
                        )
                    }
                }
            }
        }
    }
}