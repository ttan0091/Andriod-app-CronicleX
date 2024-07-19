package com.example.chronicle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.chronicle.components.BottomNavigationBar
import com.example.chronicle.roomdatabase.Setting.SettingViewModel
import com.example.chronicle.screens.PrivacyPolicyDialog
import com.example.chronicle.ui.theme.MyApplicationTheme
import com.example.chronicle.utils.LocationPermissionUtils
import com.example.chronicle.utils.PreferencesUtil
import com.example.chronicle.viewmodel.NavigationViewModel

@RequiresApi(0)
class MainActivity : ComponentActivity() {
    private val viewModel: NavigationViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // init firebase authentication, firestore and firebase storage (ensure they are available)
        viewModel.initFirebaseAuth()
        viewModel.initFirestore()
        viewModel.initStorage()

        // init setting, used to handle the stored setting data in Room
        viewModel.initSetting(this, settingViewModel)

        // create notification channel
        createNotificationChannel()

        // the callback for request location permission, triggered depending on user's agreement
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // if user share the location, retrieve their location
                    viewModel.isLocationEnabled.value = true
                    LocationPermissionUtils.getLocationAndUpdateViewModel(this, viewModel)
                    viewModel.handleLocationSwitch(true, this, requestPermissionLauncher, settingViewModel)

                } else {
                    // if user reject the permission, clear up the location data
                    viewModel.isLocationEnabled.value = false
                    viewModel.clearLocationData()
                    viewModel.handleLocationSwitch(false, this, requestPermissionLauncher, settingViewModel)
                }
            }

        // check whether user agree with the policy and popup the dialog if they are not agree
        if (!PreferencesUtil.hasAcceptedPrivacyPolicy(this)) {
            showPrivacyPolicyDialog()
        } else {
            initializeApp(requestPermissionLauncher)
        }
    }

    /**
     * notification channel used for sending reminder notification message
     * **/
    private fun createNotificationChannel() {
        // create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "reminder channel"
            val descriptionText = "channel for reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("reminder", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * method to init the app, it will not be triggered if user doesn't accept the privacy policy
     * **/
    private fun initializeApp(requestPermissionLauncher: ActivityResultLauncher<String>) {
        setContent {
            MyApplicationTheme(navViewModel = viewModel) {
                BottomNavigationBar(
                    navViewModel = viewModel,
                    requestPermissionLauncher = requestPermissionLauncher,
                    settingViewModel = settingViewModel
                )
            }
        }
    }

    /**
     * displaying the privacy policy dialog, if accepted it calls the init app
     * if not, quit the app
     * **/
    private fun showPrivacyPolicyDialog() {
        setContent {
            MyApplicationTheme(navViewModel = viewModel)  {
                PrivacyPolicyDialog(
                    showDialog = true,
                    onAccept = {
                        PreferencesUtil.acceptPrivacyPolicy(applicationContext)
                        initializeApp(requestPermissionLauncher)
                    },
                    onDecline = {
                        finish()
                    }
                )
            }
        }
    }
}