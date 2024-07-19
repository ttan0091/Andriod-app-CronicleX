package com.example.chronicle.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * encapsulated component to shows the selected images from the device in addEventScreen
 *
 * uri: the uri to the local device images
 * onDelete: the callback triggered when user press delete button
 * **/
@Composable
fun ImagePreviewCard(uri: Uri?, onDelete: () -> Unit) {
    val deleteButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier.padding(4.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        ElevatedButton(
            modifier = Modifier,
            colors = deleteButtonColors,
            onClick = {
                onDelete()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.RemoveCircle,
                contentDescription = null
            )
        }
    }
}