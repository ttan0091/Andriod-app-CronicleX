package com.example.chronicle.screens

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.chronicle.R
import com.example.chronicle.components.SettingDropdownCard
import com.example.chronicle.components.SettingNoSwitchCard
import com.example.chronicle.components.SettingSwitchCardDarkmode
import com.example.chronicle.components.SettingSwitchCardLocation
import com.example.chronicle.roomdatabase.Setting.SettingViewModel
import com.example.chronicle.viewmodel.NavigationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(
    navController: NavController,
    navViewModel: NavigationViewModel,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    settingViewModel: SettingViewModel
) {

    settingViewModel.allSettings.observeAsState().value?.let { settings ->
        Log.w("havedata", "data: $settings")
    }

    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )

    val photoUrl by remember { mutableStateOf(navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()?.currentUser?.photoUrl) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                onDraw = {
                    drawRect(Brush.linearGradient(gradientColors))
                })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .padding(top = 50.dp)
                    .padding(start = 30.dp)
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
                    .offset(y = 200.dp)
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.Top
            ) {

                SettingSwitchCardDarkmode(
                    description = stringResource(R.string.dark_model),
                    onCheckedChange = {
                        navViewModel.handleDarkModeSwitch(it, settingViewModel)
                    },
                    icon = Icons.Filled.DarkMode,
                    navViewModel = navViewModel
                )
                val currentActivity = LocalContext.current

                SettingSwitchCardLocation(
                    description = stringResource(R.string.location),
                    onCheckedChange = {
                        navViewModel.handleLocationSwitch(
                            it,
                            currentActivity,
                            requestPermissionLauncher,
                            settingViewModel
                        )
                    },
                    navViewModel = navViewModel,
                    icon = Icons.Filled.LocationOn
                )
                SettingDropdownCard(
                    description = stringResource(R.string.language),
                    icon = Icons.Filled.Language,
                    navViewModel = navViewModel,
                    onOptionClick = { option ->
                        navViewModel.selectedLanguage.value = option
                        navViewModel.handleLanguageSwitch(option, settingViewModel)
                        navViewModel.setAppLocale(currentActivity, option)
                    },
                )
                SettingNoSwitchCard(
                    description = stringResource(R.string.help),
                    onClick = {
                    val intent = Intent(Intent.ACTION_DIAL)
                        .apply {
                        data = Uri.parse("tel:0466093058")
                        }
                    currentActivity.startActivity(intent)
                    },
                    icon = Icons.Filled.Call
                )

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    val fakeLauncher: ActivityResultLauncher<String> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val fakeNavViewModel = NavigationViewModel()
    val fakeSettingViewModel = SettingViewModel(Application())
    SettingScreen(
        navController = rememberNavController(),
        navViewModel = fakeNavViewModel,
        requestPermissionLauncher = fakeLauncher,
        settingViewModel = fakeSettingViewModel
    )
}