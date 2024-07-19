package com.example.chronicle.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.chronicle.data.Event
import com.example.chronicle.navigation.Routes
import com.example.chronicle.roomdatabase.Setting.SettingViewModel
import com.example.chronicle.utils.NetworkUtil
import com.example.chronicle.viewmodel.NavigationViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OverviewScreen(
    navController: NavController,
    navViewModel: NavigationViewModel,
    settingViewModel: SettingViewModel
) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    var reminderSelected by remember { mutableStateOf(true) }
    var eventSelected by remember { mutableStateOf(true) }
    var thoughtSelected by remember { mutableStateOf(true) }
    var otherSelected by remember { mutableStateOf(true) }
    var myEvents: List<Event> by remember { mutableStateOf(emptyList()) }
    var isFetchingData by remember { mutableStateOf(true) }

    val currentDate = LocalDate.now()

    val monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH)
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH)
    val currentMonth = currentDate.format(monthFormatter)
    val currentYear = currentDate.format(yearFormatter)


    val historyMonth = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val historyYear = listOf(
        "2023", "2024", "2025", "2026", "2027", "2028", "2029",
        "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038"
    )

    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }
    var isExpandedMouth by remember { mutableStateOf(false) }
    var isExpandedYear by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(navViewModel) {
        val isNetworkAvailable = NetworkUtil().isNetworkAvailable(context)
        if (isNetworkAvailable) {
            navViewModel.getFirestoreManager()?.getMyEvent { events ->
                myEvents = events
                isFetchingData = false
                val gson = Gson()
                val historyString = gson.toJson(events)
                navViewModel.saveHistoryRoom(historyString, settingViewModel)
            }
        } else {
            isFetchingData = false
            val historyJson = settingViewModel.getSetting(1)?.diaryHistory
            if (!historyJson.isNullOrEmpty()) {
                val gson = Gson()
                val eventType = object : TypeToken<List<Event>>() {}.type
                val events = gson.fromJson<List<Event>>(historyJson, eventType)
                myEvents = events
                isFetchingData = false
            }
        }
    }

    if (isFetchingData) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Full-screen spinner
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .padding(10.dp),
                    onClick = {
                        navController.navigate(Routes.Calendar.value) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Calendar")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(70.dp))

                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Select Tag:")
                    Spacer(modifier = Modifier.width(4.dp))

                    FilterChip(
                        onClick = {
                            reminderSelected = !reminderSelected

                        },
                        label = {
                            Text("Reminder")
                        },
                        selected = reminderSelected,
                        leadingIcon = if (reminderSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    FilterChip(
                        onClick = { eventSelected = !eventSelected },
                        label = {
                            Text("Event")
                        },
                        selected = eventSelected,
                        leadingIcon = if (eventSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Spacer(modifier = Modifier.width(100.dp))

                    FilterChip(
                        onClick = { thoughtSelected = !thoughtSelected },
                        label = {
                            Text("Thought")
                        },
                        selected = thoughtSelected,
                        leadingIcon = if (thoughtSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    FilterChip(
                        onClick = { otherSelected = !otherSelected },
                        label = {
                            Text("Other")
                        },
                        selected = otherSelected,
                        leadingIcon = if (otherSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    ExposedDropdownMenuBox(
                        expanded = isExpandedMouth,
                        onExpandedChange = { isExpandedMouth = it }
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
                            value = selectedMonth,
                            onValueChange = { },
                            label = { Text(text = "Select Month") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedMouth) }
                        )
                        ExposedDropdownMenu(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.surface),
                            expanded = isExpandedMouth,
                            onDismissRequest = { isExpandedMouth = false },
                        ) {
                            historyMonth.forEach { selectionOption ->
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
                                        selectedMonth = selectionOption
                                        isExpandedMouth = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ExposedDropdownMenuBox(
                        expanded = isExpandedYear,
                        onExpandedChange = { isExpandedYear = it }
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
                            value = selectedYear,
                            onValueChange = { },
                            label = { Text(text = "Select Year") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedYear) }
                        )
                        ExposedDropdownMenu(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.surface),
                            expanded = isExpandedYear,
                            onDismissRequest = { isExpandedYear = false },
                        ) {
                            historyYear.forEach { selectionOption ->
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
                                        selectedYear = selectionOption
                                        isExpandedYear = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    val eventGroups =
                        myEvents.groupBy { it.getEventDate() }.toSortedMap(reverseOrder())

                    val selectedMonthEnum: Month = Month.valueOf(selectedMonth.uppercase())

                    val selectedYearEnum: Year = Year.parse(selectedYear.uppercase())

                    val eventsInSelectedMonthAndYear = myEvents.filter { event ->
                        // 添加条件来检查事件是否在所选月份和年份中
                        val eventDate = event.getEventDate()
                        val eventDateYear: Year = Year.of(eventDate.year)
                        eventDate.month == selectedMonthEnum && eventDateYear == selectedYearEnum
                    }


                    if (eventsInSelectedMonthAndYear.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp)
                        ) {
                            eventGroups.forEach { (eventDate, events) ->
                                stickyHeader {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.secondaryContainer
                                            ),
                                        text = eventDate.toString(),
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                itemsIndexed(events) { index, event ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    if (event.tag == "Event" && eventSelected) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .clickable {
                                                    navViewModel.selectedEvent.value = event
                                                    navController.navigate(Routes.EventDetail.value) {
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(start = 20.dp),
                                                    text = "Event: ${event.title}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                        }
                                    } else if (event.tag == "Reminder" && reminderSelected) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .clickable {
                                                    navViewModel.selectedEvent.value = event
                                                    navController.navigate(Routes.EventDetail.value) {
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(start = 20.dp),
                                                    text = "Reminder: ${event.title}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    } else if (event.tag == "Thought" && thoughtSelected) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .clickable {
                                                    navViewModel.selectedEvent.value = event
                                                    navController.navigate(Routes.EventDetail.value) {
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(start = 20.dp),
                                                    text = "Thought: ${event.title}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    } else if (event.tag == "Other" && otherSelected) {
                                        ElevatedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .clickable {
                                                    navViewModel.selectedEvent.value = event
                                                    navController.navigate(Routes.EventDetail.value) {
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(start = 20.dp),
                                                    text = "Other: ${event.title}",
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 100.dp),
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.titleMedium,
                            text = "Seems Nothing Recorded For This Month",
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }
}