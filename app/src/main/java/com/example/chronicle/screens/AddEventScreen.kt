package com.example.chronicle.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.example.chronicle.components.ImagePreviewCard
import com.example.chronicle.components.MapDialog
import com.example.chronicle.data.Event
import com.example.chronicle.utils.ReminderAlarm
import com.example.chronicle.utils.formatSelectedDate
import com.example.chronicle.viewmodel.NavigationViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEventScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    var selectedDate =
        Instant.ofEpochMilli(navViewModel.selectedDate.value).atZone(ZoneId.systemDefault())
            .toLocalDate()
    val titleText = rememberSaveable { mutableStateOf("") }
    val bodyText = rememberSaveable { mutableStateOf("") }
    val locationText = rememberSaveable { mutableStateOf("") }
    val dateText = rememberSaveable { mutableStateOf("") }
    val formattedDate = rememberSaveable { mutableStateOf(selectedDate.toString()) }
    val timePickerStateVertical = rememberTimePickerState()
    var selectedHour = timePickerStateVertical.hour
    var selectedMinute = timePickerStateVertical.minute
    val formattedTime = rememberSaveable {
        mutableStateOf(
            String.format(
                "%02d:%02d",
                selectedHour,
                selectedMinute
            )
        )
    }
    val isShared = rememberSaveable { mutableStateOf(false) }
    val states = listOf("Event", "Reminder", "Thought", "Other")
    var isExpanded by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf(states[0]) }
    val openMap = remember { mutableStateOf(false) }
    val discardButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )
    val context = LocalContext.current
    val selectedImageUri = remember { mutableStateListOf<Uri?>() }
    val selectedLocationText = remember { mutableStateOf("") }
    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (!selectedImageUri.contains(it)) {
                selectedImageUri.add(it)
            }
        }
    }
    val currentWeather by navViewModel.currentWeather
    var isTimePickerOpen by remember { mutableStateOf(false) }
    var isTimeSaved by remember { mutableStateOf(false) }
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var isDateSaved by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
    )
    var isSaving by remember { mutableStateOf(false) }
    val showPermissionDialog = navViewModel.showPermissionDialog.collectAsState().value
    val scrollState = rememberScrollState()

    fun clearInputs() {
        titleText.value = ""
        bodyText.value = ""
        locationText.value = ""
        selectedLocationText.value = ""
        navViewModel.currentWeather.value = ""
        formattedDate.value = ""
        formattedTime.value = ""
        isShared.value = false
        selectedState = states[0]
        selectedImageUri.clear()
    }
    Scaffold(
        floatingActionButton = {
            // hide the floating button if scrolling down
            if (scrollState.value < 150) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("ChatBot") },
                    icon = {
                        Icon(
                            Icons.Filled.ChatBubble,
                            contentDescription = "Chat with bot",
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    text = { Text("Chat with AI") }
                )
            }
        }
    ) {
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
                    text = currentWeather,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.height(30.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    TextField(
                        placeholder = {
                            Text(
                                text = "* Title...",
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
                                text = "* Description...",
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
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            ),
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
                            states.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            text = selectionOption
                                        )
                                    },
                                    onClick = {
                                        selectedState = selectionOption
                                        isExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

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
                                    text = "Date...",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            maxLines = 1,
                            enabled = false,
                            value = formattedDate.value,
                            onValueChange = { },
                            textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.surface
                            )
                        )


                        if (isDateSaved) {
                            dateText.value = formattedDate.value
                            ElevatedButton(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 10.dp),
                                onClick = { isDatePickerOpen = true }) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Select Date")
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.Event,
                                        contentDescription = null
                                    )
                                }

                            }
                        } else {
                            ElevatedButton(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 10.dp),
                                onClick = { isDatePickerOpen = true }) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Select Date")
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.Event,
                                        contentDescription = null
                                    )
                                }

                            }
                        }

                        if (isDatePickerOpen) {
                            DatePickerDialog(
                                onDismissRequest = { isDatePickerOpen = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        isDateSaved = true
                                        isDatePickerOpen = false
                                        navViewModel.setSelectedDate(datePickerState.selectedDateMillis!!)
                                        selectedDate =
                                            Instant.ofEpochMilli(navViewModel.selectedDate.value)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDate()
                                        formattedDate.value = selectedDate.toString()
                                    }) {
                                        Text("Save")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { isDatePickerOpen = false }) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState, showModeToggle = false)
                            }
                        }
                    }


                    if (selectedState == "Reminder") {

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
                                enabled = false,
                                placeholder = {
                                    Text(
                                        text = "Time Picker",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                },
                                maxLines = 1,
                                value = formattedTime.value,
                                onValueChange = { },
                                textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.surface
                                )
                            )

                            if (isTimeSaved) {
                                ElevatedButton(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 10.dp),
                                    onClick = { isTimePickerOpen = true }) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Select Time")
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null
                                        )
                                    }
                                }
                            } else {
                                ElevatedButton(
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 10.dp),
                                    onClick = { isTimePickerOpen = true }) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Select Time")
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null
                                        )
                                    }

                                }
                            }

                            if (isTimePickerOpen) {
                                AlertDialog(
                                    onDismissRequest = { isTimePickerOpen = false },
                                    title = { Text("Select Time") },
                                    buttons = {
                                        ElevatedButton(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .padding(end = 10.dp),
                                            onClick = {
                                                isTimeSaved = true
                                                isTimePickerOpen = false
                                                selectedHour = timePickerStateVertical.hour
                                                selectedMinute = timePickerStateVertical.minute
                                                formattedTime.value = String.format(
                                                    "%02d:%02d",
                                                    selectedHour,
                                                    selectedMinute
                                                )

                                                with(NotificationManagerCompat.from(context)) {
                                                    if (ActivityCompat.checkSelfPermission(
                                                            context,
                                                            Manifest.permission.POST_NOTIFICATIONS
                                                        ) != PackageManager.PERMISSION_GRANTED
                                                    ) {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                            ActivityCompat.requestPermissions(
                                                                context as Activity,
                                                                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                                                101
                                                            )
                                                        }
                                                        Log.e(
                                                            "PushNotification",
                                                            "Missing permission: POST_NOTIFICATIONS"
                                                        )
                                                        return@with
                                                    }
                                                    Log.d(
                                                        "PushNotification",
                                                        "Posting notification"
                                                    )
                                                }
                                            }) {

                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "Save")
                                            }
                                        }
                                    },
                                    text = {
                                        TimePicker(
                                            state = timePickerStateVertical,
                                            layoutType = TimePickerLayoutType.Vertical
                                        )
                                    }
                                )
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
                            enabled = false,
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
                            onClick = { openMap.value = true }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = selectedLocationText.value)
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null
                                )
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
                            value = currentWeather,
                            onValueChange = { navViewModel.currentWeather.value = it },
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

                            onClick = { navViewModel.fetchWeatherData() }) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Get Weather")
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(imageVector = Icons.Outlined.Cloud, contentDescription = null)
                            }
                        }

                        if (showPermissionDialog) {
                            AlertDialog(
                                onDismissRequest = { navViewModel.resetPermissionDialog() },
                                text = { Text("You should get your location first.") },
                                confirmButton = {
                                    TextButton(onClick = { navViewModel.resetPermissionDialog() }) {
                                        Text("Sure")
                                    }
                                }
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (selectedState != states[1]) {
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
                                onValueChange = {},
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
                                colors = SwitchDefaults.colors(
                                    uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    checkedTrackColor = MaterialTheme.colorScheme.secondary
                                ),
                                onCheckedChange = {
                                    isShared.value = it
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (!isSaving) {
                            ElevatedButton(
                                onClick = {
                                    if (bodyText.value.isNotEmpty() && titleText.value.isNotEmpty()) {
                                        isSaving = true
                                        val downloadUrls = mutableStateListOf<String>()
                                        var count = 0
                                        if (selectedImageUri.isEmpty()) {
                                            val event = Event(
                                                title = titleText.value,
                                                body = bodyText.value,
                                                date = navViewModel.getDate(),
                                                time = formattedTime.value,
                                                images = downloadUrls.toList(),
                                                location = selectedLocationText.value,
                                                weather = currentWeather,
                                                isPublic = isShared.value,
                                                tag = selectedState
                                            )
                                            navViewModel.getFirestoreManager()?.addEvent(event) {
                                                Toast.makeText(
                                                    context,
                                                    "Event saved",
                                                    Toast.LENGTH_SHORT,
                                                ).show()
                                                isSaving = false
                                                clearInputs()
                                            }
                                        } else {
                                            for (uri in selectedImageUri) {
                                                navViewModel.getStorageManager()
                                                    ?.uploadImages(uri!!) {
                                                        Log.d("URLS", "IT ${it}")
                                                        downloadUrls.add(it)
                                                        count++
                                                        if (count == selectedImageUri.size) {

                                                            val event = Event(
                                                                title = titleText.value,
                                                                body = bodyText.value,
                                                                date = navViewModel.getDate(),
                                                                time = formattedTime.value,
                                                                images = downloadUrls.toList(),
                                                                location = selectedLocationText.value,
                                                                weather = currentWeather,
                                                                isPublic = isShared.value,
                                                                tag = selectedState
                                                            )

                                                            navViewModel.getFirestoreManager()
                                                                ?.addEvent(event) {
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Saved",
                                                                        Toast.LENGTH_SHORT,
                                                                    ).show()
                                                                    isSaving = false
                                                                    clearInputs()
                                                                }
                                                        }
                                                    }
                                            }
                                        }
                                        if (selectedState == "Reminder") {
                                            setAlarm(
                                                context,
                                                titleText.value,
                                                bodyText.value,
                                                selectedDate,
                                                selectedHour,
                                                selectedMinute
                                            )
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please Enter Title and Description",
                                            Toast.LENGTH_SHORT,
                                        ).show()
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
                                onClick = {
                                    clearInputs()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(start = 4.dp),
                                colors = discardButtonColors
                            ) {
                                Text(text = "Discard")
                            }
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

/**
 * used to push the notification with inputted content and notify on specific time
 *
 * context: current context
 * title: notification title / reminder title
 * message: notification message / reminder body
 * selectedDate: reminder date
 * selectedHour: reminder hour
 * selectedMinute: reminder minute
 * **/
@RequiresApi(Build.VERSION_CODES.O)
private fun setAlarm(
    context: Context,
    title: String,
    message: String,
    selectedDate: LocalDate,
    selectedHour: Int,
    selectedMinute: Int
) {
    val selectedDateTime =
        LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute))
//    val timeSec = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val timeSec =
        System.currentTimeMillis() + 3000 // todo Needed for demo, display notification in 3 secs
    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
    // store notification data in SharedPreferences (intent extra fails due to some reason)
    val sharedPreferences = context.getSharedPreferences("AlarmData", Context.MODE_PRIVATE)
    sharedPreferences.edit().apply {
        putString("title", title)
        putString("message", message)
        apply()
    }
    // specify BroadcastReceiver to handle the alarm/notification
    val intent = Intent(context, ReminderAlarm::class.java)
    val pendingIntent =
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    alarmManager.set(AlarmManager.RTC_WAKEUP, timeSec, pendingIntent)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    AddEventScreen(
        navController = NavController(LocalContext.current),
        navViewModel = NavigationViewModel()
    )
}