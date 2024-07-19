package com.example.chronicle.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * the button/card with no extra component in setting (e.g. used for help)
 * **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingNoSwitchCard(description: String, onClick: (route: String) -> Unit, icon: ImageVector) {

    Card(
        onClick = { onClick(description) },
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { /*@todo*/ }
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                imageVector = icon,
                contentDescription = description
            )
            Text(
                text = description,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview()
@Composable
fun SettingNoSwitchCardPreview() {
    SettingNoSwitchCard("Profile", {}, Icons.Filled.AccountBox)
}