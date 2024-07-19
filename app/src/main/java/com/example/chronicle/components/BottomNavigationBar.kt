package com.example.chronicle.components

import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chronicle.data.NavBarItem
import com.example.chronicle.navigation.Routes
import com.example.chronicle.roomdatabase.Setting.SettingViewModel
import com.example.chronicle.screens.AddEventScreen
import com.example.chronicle.screens.CalendarScreen
import com.example.chronicle.screens.ChatBotScreen
import com.example.chronicle.screens.EditEventScreen
import com.example.chronicle.screens.EventDetailScreen
import com.example.chronicle.screens.EventScreen
import com.example.chronicle.screens.HistoryScreen
import com.example.chronicle.screens.LoginScreen
import com.example.chronicle.screens.OverviewScreen
import com.example.chronicle.screens.ProfileScreen
import com.example.chronicle.screens.RegistrationScreen
import com.example.chronicle.screens.SettingScreen
import com.example.chronicle.utils.ApiClient
import com.example.chronicle.viewmodel.NavigationViewModel

/**
 *  encapsulated bottom navigation bar
 *
 *  navViewModel: viewModel that has all the necessary methods and data to support functions
 *  requestPermissionLauncher: ActivityResultLauncher used to handle the location permission operation
 *  icon: the icon on the button
 * **/
@RequiresApi(0)
@Composable
fun BottomNavigationBar(
    navViewModel: NavigationViewModel,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    settingViewModel: SettingViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var selectedNavItem by remember { mutableStateOf(0) }

    // reset the app language
    val currentContext = LocalContext.current
    LaunchedEffect(navViewModel.selectedLanguage.value) {
        navViewModel.setAppLocale(currentContext, navViewModel.selectedLanguage.value)
    }

    // add destination changed listener, used to handle the highlighting changes on navigation items
    // remove listener after disposable effect is removed from composition
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            // update selectedNavItem based on the destination
            selectedNavItem = when (destination.route) {
                Routes.Calendar.value -> 0
                Routes.Event.value -> 1
                Routes.AddEvent.value -> 2
                Routes.Profile.value -> 3
                else -> selectedNavItem
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentDestination?.route != Routes.Login.value && currentDestination?.route != Routes.Registration.value) {
                NavigationBar {
                    NavBarItem().NavBarItems(LocalContext.current)
                        .forEachIndexed { index, navItem ->
                            BottomNavigationItem(
                                icon = {
                                    TabBarIconView(
                                        // if the destination match the nav bar item index
                                        // highlight the item
                                        isSelected = selectedNavItem == index,
                                        selectedIcon = navItem.selectedIcon,
                                        unselectedIcon = navItem.unSelectedIcon,
                                        title = navItem.label
                                    )
                                },
                                selected = selectedNavItem == index,
                                onClick = {
                                    navController.navigate(navItem.route) {
                                        selectedNavItem = index
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = getStartDestination(
                navViewModel.getFirebaseAuthManager()
                    ?.getFirebaseAuth()?.currentUser != null
            )
        ) {
            composable(Routes.Login.value) {
                LoginScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.Registration.value) {
                RegistrationScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.Calendar.value) {
                CalendarScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.Profile.value) {
                ProfileScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.Event.value) {
                EventScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.History.value) {
                HistoryScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.AddEvent.value) {
                AddEventScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.EditEvent.value) {
                EditEventScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.Setting.value) {
                SettingScreen(
                    navController = navController,
                    navViewModel,
                    requestPermissionLauncher,
                    settingViewModel
                )
            }
            composable(Routes.EventDetail.value) {
                EventDetailScreen(
                    navController = navController,
                    navViewModel
                )
            }
            composable(Routes.Overview.value) {
                OverviewScreen(
                    navController = navController,
                    navViewModel,
                    settingViewModel
                )
            }

            composable(Routes.ChatBot.value) {
                ChatBotScreen(navController, ApiClient.getInstance())
            }
        }
    }
}

/**
 * checking if the user auth still available, if it is then navigate to home screen
 * if not, request login.
 * **/
fun getStartDestination(isUserLoggedIn: Boolean): String {
    return if (isUserLoggedIn) {
        Routes.Calendar.value
    } else {
        Routes.Login.value
    }
}

/**
 * encapsulated icon view in the bottom navigation bar, to display the highlighting
 *
 * isSelected: shows whether the item is selected
 * selectedIcon: icon displayed if it is selected
 * unselectedIcon: icon displayed if it is NOT selected
 * title: the text underneath
 * badgeAmount: number displaying on the item (NOT USED)
 * **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(50.dp)
                .padding(top = 11.dp, bottom = 11.dp)
                .clip(RoundedCornerShape(50.dp)),
            color = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Transparent
            }
        ) {
            BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(),
                    imageVector = if (isSelected) {
                        selectedIcon
                    } else {
                        unselectedIcon
                    },
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    },
                    contentDescription = title
                )
            }
        }
        Text(
            modifier = Modifier
                .padding(top = 50.dp),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            text = title
        )
    }
}

/**
 * used to handle the badge shows the number on nav item (e.g. numbers of unread messages)
 * it's not used in the app but keep it maybe for future use
 * **/
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

@Preview
@Composable
fun BottomNavigationPreview() {
//    BottomNavigationBar(NavigationViewModel())
}