package com.example.chronicle.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chronicle.viewmodel.NavigationViewModel

/**
 * the button/card with switch in setting (e.g. used for location permission)
 * **/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingSwitchCardLocation(
    description: String,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    navViewModel: NavigationViewModel
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
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
            Switch(
                modifier = Modifier.padding(end = 30.dp),
                checked = navViewModel.isLocationEnabled.value,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

        }
    }
}

@Preview()
@Composable
fun SettingSwitchCardPreview() {
//    SettingSwitchCard("设置", {}, Icons.Filled.AccountBox, false)
}