package com.example.chronicle.screens

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chronicle.components.ImagePreviewCard
import com.example.chronicle.components.MapDialog
import com.example.chronicle.data.Event
import com.example.chronicle.utils.formatSelectedDate
import com.example.chronicle.viewmodel.NavigationViewModel
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditEventScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
    val selectedDate = Instant.ofEpochMilli(navViewModel.selectedDate.value).atZone(ZoneId.systemDefault()).toLocalDate()
    val selectedEvent = navViewModel.selectedEvent.value

    val titleText = rememberSaveable { mutableStateOf(selectedEvent.title) }
    val bodyText = rememberSaveable { mutableStateOf(selectedEvent.body) }
    val locationText = rememberSaveable { mutableStateOf(selectedEvent.location) }
    val isShared = rememberSaveable { mutableStateOf(selectedEvent.isPublic) }
    val states = listOf("Event", "Reminder", "Thought", "Other")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(states[0]) }

    val openMap = remember{ mutableStateOf(false) }
    val discardButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )
    val context = LocalContext.current
    val selectedImageUri = remember { mutableStateListOf<Uri?>() }
    val selectedLocationText = remember { mutableStateOf("View Map") }

    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (!selectedImageUri.contains(it)) {
                selectedImageUri.add(it)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = formatSelectedDate(selectedDate),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = navViewModel.currentWeather.value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
            ) {
                TextField(
                    placeholder = {
                        Text(
                            text = "Title...",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    maxLines = 1,
                    value = titleText.value,
                    onValueChange = { titleText.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    placeholder = {
                        Text(
                            text = "Description...",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    maxLines = 20,
                    value = bodyText.value,
                    onValueChange = { bodyText.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    ElevatedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { getContent.launch("image/*") }) {
                        Text("Upload Photo")
                    }
                    selectedImageUri.forEachIndexed { index, uri ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            ImagePreviewCard(uri = uri) {
                                selectedImageUri.removeAt(index)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        placeholder = {
                            Text(
                                text = "Location...",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        maxLines = 1,
                        value = locationText.value,
                        onValueChange = { locationText.value = it },
                        textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    ElevatedButton(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp),
                        onClick = {openMap.value = true}) {
                        Row (
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = selectedLocationText.value)
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null)
                        }
                    }

                    if (openMap.value) {
                        MapDialog(
                            viewModel = navViewModel,
                            onDismissRequest = { openMap.value = false },
                            onConfirmation = { locationText ->
                                openMap.value = false
                                selectedLocationText.value = locationText
                                println("Confirmation registered: $locationText")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        placeholder = {
                            Text(
                                text = "Weather...",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        maxLines = 1,
                        value = locationText.value,
                        onValueChange = { locationText.value = it },
                        textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    ElevatedButton(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 10.dp),
                        onClick = { /*TODO*/ }) {
                        Row (
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(text = "Auto Fill")
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(imageVector = Icons.Outlined.Cloud, contentDescription = null)
                        }

                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if(selectedState != states[1]) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            enabled = false,
                            placeholder = {
                                Text(
                                    text = "Share Event With Everyone?",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            maxLines = 1,
                            value = "",
                            onValueChange = { locationText.value = it },
                            textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Switch(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 10.dp),
                            checked = isShared.value,
                            onCheckedChange = {
                                isShared.value = it
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }
                ) {

                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .focusProperties {
                                canFocus = false
                            }
                            .padding(bottom = 8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        ),
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary),
                        readOnly = true,
                        value = selectedState,
                        onValueChange = { },
                        label = { Text(text = "Tag") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                    )
                    ExposedDropdownMenu(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surface),
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false },
                    ) {
                        states.forEach { selectionOption -> DropdownMenuItem(
                            text = { Text(
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                text = selectionOption)
                            },
                            onClick = {
                                selectedState = selectionOption
                                isExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        onClick = {
                            val downloadUrls = mutableStateListOf<String>()
                            var count = 0
                            if (selectedImageUri.isEmpty()) {
                                val event = Event(
                                    eventId = selectedEvent.eventId,
                                    title = titleText.value,
                                    body = bodyText.value,
                                    date = navViewModel.getDate(),
                                    time = "",
                                    images = downloadUrls.toList(),
                                    location = "Event Location",
                                    weather = "Event Weather",
                                    isPublic = isShared.value,
                                    tag = selectedState
                                )
                                navViewModel.getFirestoreManager()?.editEvent(event) {
                                    Toast.makeText(
                                        context,
                                        "Event edited",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    navController.popBackStack()
                                }
                                navController.popBackStack()
                            }
                            else {
                                for (uri in selectedImageUri) {
                                    navViewModel.getStorageManager()?.uploadImages(uri!!) {
                                        Log.d("URLS", "IT ${it}")
                                        downloadUrls.add(it)
                                        count ++
                                        if (count == selectedImageUri.size) {

                                            val event = Event(
                                                eventId = selectedEvent.eventId,
                                                title = titleText.value,
                                                body = bodyText.value,
                                                date = navViewModel.getDate(),
                                                time = "",
                                                images = downloadUrls.toList(),
                                                location = "Event Location",
                                                weather = "Event Weather",
                                                isPublic = isShared.value,
                                                tag = selectedState
                                            )
                                            navViewModel.getFirestoreManager()?.editEvent(event) {
                                                Toast.makeText(
                                                    context,
                                                    "Edited",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                                navController.popBackStack()
                                            }
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) {
                        Text(text = "Save")
                    }

                    ElevatedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 4.dp),
                        colors = discardButtonColors
                    ) {
                        Text(text = "Discard")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EditEventScreenPreview() {
    EditEventScreen(navController = NavController(LocalContext.current), navViewModel = NavigationViewModel())
}