package com.example.chronicle.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
 *  encapsulated button element on profile screen
 *
 *  description: the text on the button
 *  onClick: callback method after clicking
 *  icon: the icon on the button
 * **/
@Composable
fun AccountOptionCard(description: String, onClick: () -> Unit, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() },
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
            Icon(
                modifier = Modifier.padding(end = 20.dp),
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = description
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Preview(showBackground = true)
@Composable
fun AccountOptionCardPreview() {
    AccountOptionCard("Profile", {}, Icons.Filled.AccountBox)
}