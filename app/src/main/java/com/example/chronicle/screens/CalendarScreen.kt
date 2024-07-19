package com.example.chronicle.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.chronicle.R
import com.example.chronicle.navigation.Routes
import com.example.chronicle.viewmodel.NavigationViewModel
import java.time.Instant
import java.util.Calendar


@RequiresApi(0)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.tertiaryContainer

    )
    Calendar.getInstance()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

    val scaffoldState = rememberScaffoldState()
    remember {
        mutableStateOf(false)
    }

    val fullName = navViewModel.getFirebaseAuthManager()?.getUserName() ?: "Guest"
    val firstName = fullName.split(" ").first()

    Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.secondaryContainer,
        )
    )

    val MontserratFontFamily = FontFamily(
        Font(R.font.designed_font)
    )

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navController.navigate(Routes.History.value) {
                        navViewModel.setSelectedDate(datePickerState.selectedDateMillis!!)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = "View Event"
                    )
                },
                text = { Text(text = "View This day") }
            )
        }) {
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        navController.navigate(Routes.Overview.value) {
                            navViewModel.setSelectedDate(datePickerState.selectedDateMillis!!)
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
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "History")
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 60.dp)
                        .fillMaxWidth()
                        .offset(y = 90.dp)
                        .height(50.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "Hello $firstName",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = MontserratFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                DatePicker(
                    modifier = Modifier
                        .offset(y = 120.dp),
                    state = datePickerState,
                    showModeToggle = false      // not allow manual input from user - error prevention
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CalendarScreen(
        navController = NavController(LocalContext.current),
        navViewModel = NavigationViewModel()
    )
}