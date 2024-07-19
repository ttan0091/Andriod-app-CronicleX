package com.example.chronicle.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.chronicle.data.Event
import com.example.chronicle.navigation.Routes
import com.example.chronicle.utils.formatSelectedDate
import com.example.chronicle.viewmodel.NavigationViewModel
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    val selectedDate =
        Instant.ofEpochMilli(navViewModel.selectedDate.value).atZone(ZoneId.systemDefault())
            .toLocalDate()
    val scaffoldState = rememberScaffoldState()
    var myEvents: List<Event> by remember { mutableStateOf(emptyList()) }
    var isFetchingData by remember { mutableStateOf(true) }

    LaunchedEffect(navViewModel) {
        navViewModel.getFirestoreManager()?.getMyEventOnDate(
            navViewModel.getDate()
        ) { events ->
            // Update the state with the retrieved events
            myEvents = events
            isFetchingData = false
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
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AddEvent.value) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    shape = CircleShape,
                ) {
                    if (myEvents.isNotEmpty()) {
                        Icon(Icons.Filled.Add, "Floating action button")
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(10.dp),
                            text = "Create Now!"
                        )
                    }
                }
            }
        ) {
            innerPadding -> { innerPadding }
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        onDraw = {
                            drawRect(Brush.linearGradient(gradientColors))
                        })
                    ElevatedButton(
                        modifier = Modifier
                            .offset(x = 10.dp, y = 10.dp),
                        onClick = {
                            navController.popBackStack()
                        }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = null)
                            Text(text = "Back")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(70.dp)
                            .offset(y = 133.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Your diary items on this date",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp),
                                color = MaterialTheme.colorScheme.secondary
                            )

                        }
                    }
                }
                if (myEvents.isNotEmpty()) {
                    TimelineScreen(navController, myEvents, navViewModel)
                } else {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 200.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleMedium,
                        text = "Seems Nothing Recorded For This Day",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimelineScreen(navController: NavController, myEvents: List<Event>, navViewModel: NavigationViewModel) {
    Text(
        modifier = Modifier
            .offset(y = 50.dp, x = 30.dp),
        text = "",
        style = MaterialTheme.typography.labelLarge
    )
    Row {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 10.dp)
                .weight(1f)
        ) {
            drawLine(
                color = Color.Black,
                start = Offset(size.width / 1.3.toFloat(), 130.toFloat()),
                end = Offset(size.width / 1.3.toFloat(), 2000.toFloat()),
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f)
                .padding(top = 70.dp)
                .height(390.dp)
        ) {
            myEvents.forEachIndexed {index, event ->
                if (event.tag == "Reminder") {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .wrapContentHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = "${event.tag}: ${event.title}",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = event.time,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                } else {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .wrapContentHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .wrapContentHeight()

                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .wrapContentHeight()

                                ) {
                                    Text(
                                        modifier = Modifier
                                            .weight(1f),
                                        text = "${event.tag}: ${event.title}",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = event.body,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    TextButton(
                                        onClick = {
                                            navViewModel.selectedEvent.value = myEvents.get(index)
                                            navController.navigate(Routes.EventDetail.value) {
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    ) {
                                        Text(text = "See More")
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(
        navController = NavController(LocalContext.current),
        navViewModel = NavigationViewModel()
    )
}