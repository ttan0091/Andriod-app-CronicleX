package com.example.chronicle.viewmodel

import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chronicle.R
import com.example.chronicle.RetrofitAPI
import com.example.chronicle.data.Event
import com.example.chronicle.firebase.FirebaseAuthManager
import com.example.chronicle.firebase.FirestoreManager
import com.example.chronicle.firebase.StorageManager
import com.example.chronicle.openweather.OpenWeatherApi
import com.example.chronicle.roomdatabase.Setting.Setting
import com.example.chronicle.roomdatabase.Setting.SettingViewModel
import com.example.chronicle.utils.LocationPermissionUtils
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class NavigationViewModel: ViewModel() {
    private var firebaseAuthManager: MutableState<FirebaseAuthManager?> = mutableStateOf(null)
    private var firestoreManager: MutableState<FirestoreManager?> = mutableStateOf(null)
    private var storageManager: MutableState<StorageManager?> = mutableStateOf(null)
    var selectedDate: MutableState<Long> = mutableStateOf(Instant.now().toEpochMilli())
    var selectedEvent: MutableState<Event> = mutableStateOf(Event())

    var isDarkTheme = mutableStateOf(false)
    var isLocationEnabled = mutableStateOf(false)
    var locationData: MutableState<Location?> = mutableStateOf(null)
    var selectedLanguage = mutableStateOf("English")
    var currentWeather = mutableStateOf("")
    private val _showPermissionDialog = MutableStateFlow(false)
    val showPermissionDialog = _showPermissionDialog.asStateFlow()

    private val _searchResults = MutableLiveData<List<Point>>()
    val searchResults: LiveData<List<Point>> = _searchResults
    private val _selectedLocation = MutableLiveData<Point?>()
    val selectedLocation: LiveData<Point?> = _selectedLocation
    var historyJson = mutableStateOf("")
    fun initSetting(currentContext: Context, settingViewModel: SettingViewModel) {
        viewModelScope.launch {
            val setting = settingViewModel.getSetting(1)
            if (setting != null) {
                isDarkTheme.value = setting.isDarkTheme
                isLocationEnabled.value = setting.isLocationEnabled
                selectedLanguage.value = setting.language
                setAppLocale(currentContext, setting.language)
            }
        }
    }

    fun saveHistoryRoom(history: String, settingViewModel: SettingViewModel) {
        viewModelScope.launch {
            historyJson.value = history
            val exist = settingViewModel.getSetting(1)
            if (exist != null) {
                // update existing setting data
                settingViewModel.updateSetting(exist.copy(diaryHistory = history))
            } else {
                // insert new value if there's no existing setting data
                settingViewModel.insertSetting(
                    Setting(
                        id = 1,
                        isDarkTheme = isDarkTheme.value,
                        isLocationEnabled = isLocationEnabled.value,
                        language = selectedLanguage.value,
                        diaryHistory = history
                    )
                )
            }
        }
    }
    fun handleLanguageSwitch(language: String, settingViewModel: SettingViewModel) {
        viewModelScope.launch {
            selectedLanguage.value = language
            val exist = settingViewModel.getSetting(1)
            if (exist != null) {
                // update existing language data
                settingViewModel.updateSetting(exist.copy(language = language))
            } else {
                // insert new value if there's no existing data
                settingViewModel.insertSetting(
                    Setting(
                        id = 1,
                        isDarkTheme = isDarkTheme.value,
                        isLocationEnabled = isLocationEnabled.value,
                        language = language
                    )
                )
            }
        }
    }

    fun handleDarkModeSwitch(shouldBeDark: Boolean, settingViewModel: SettingViewModel) {
        viewModelScope.launch {
            isDarkTheme.value = shouldBeDark
            val exist = settingViewModel.getSetting(1)
            if (exist != null) {
                settingViewModel.updateSetting(exist.copy(isDarkTheme = shouldBeDark))
            } else {
                settingViewModel.insertSetting(
                    Setting(
                        id = 1,
                        isDarkTheme = shouldBeDark,
                        isLocationEnabled = isLocationEnabled.value,
                        language = selectedLanguage.value
                    )
                )
            }
        }
    }

    // update location data
    fun updateLocationData(location: Location) {
        locationData.value = location
        if (locationData.value != null) {
            Log.w("new location1", "locationData: ${locationData.value}")
        } else {
            Log.w("new location1", "locationData: null")
        }
    }

    // clear location data
    fun clearLocationData() {
        locationData.value = null
        if (locationData.value != null) {
            Log.w("new location2", "locationData: ${locationData.value}")
        } else {
            Log.w("new location2", "locationData: null")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleLocationSwitch(
        shouldBeEnabled: Boolean,
        currentActivity: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>,
        settingViewModel: SettingViewModel
    ) {
        viewModelScope.launch {
            val exist = settingViewModel.getSetting(1)
            if (shouldBeEnabled) {
                if (LocationPermissionUtils.isLocationPermissionGranted(currentActivity)) {
                    isLocationEnabled.value = true
                    LocationPermissionUtils.getLocationAndUpdateViewModel(
                        currentActivity,
                        this@NavigationViewModel
                    )
                    updateDatabase(exist, settingViewModel)
                } else {
                    LocationPermissionUtils.handleLocationPermissionRequest(
                        requestPermissionLauncher
                    )
                }
            } else {
                isLocationEnabled.value = false
                clearLocationData()
                updateDatabase(exist, settingViewModel)
            }
        }
    }

    private suspend fun updateDatabase(exist: Setting?, settingViewModel: SettingViewModel) {
        if (exist == null) {
            settingViewModel.insertSetting(
                Setting(
                    1,
                    isDarkTheme = isDarkTheme.value,
                    isLocationEnabled = isLocationEnabled.value,
                    language = selectedLanguage.value
                )
            )
        } else {
            settingViewModel.updateSetting(exist.copy(isLocationEnabled = isLocationEnabled.value))
        }
    }

    fun setAppLocale(context: Context, option: String) {
        val languageCodes = mapOf(
            "English" to "en",
            "简体中文" to "zh",
            "Français" to "fr",
            "Deutsch" to "de",
            "日本語" to "ja",
            "한국어" to "ko"
        )
        val languageCode = languageCodes[option] ?: "en"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedDate(newDate: Long) {
        selectedDate.value = newDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(): String {
        val selectedDateInMillis = selectedDate.value
        val eventDate = Instant.ofEpochMilli(selectedDateInMillis)
        val zonedDateTime = ZonedDateTime.ofInstant(eventDate, ZoneId.systemDefault())
        val localDate = zonedDateTime.toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return localDate.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initFirebaseAuth() {
        firebaseAuthManager = mutableStateOf(FirebaseAuthManager())
    }

    fun initFirestore() {
        firestoreManager = mutableStateOf(FirestoreManager())
    }

    fun initStorage() {
        storageManager = mutableStateOf(StorageManager())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFirebaseAuthManager(): FirebaseAuthManager? {
        return firebaseAuthManager.value
    }

    fun getFirestoreManager(): FirestoreManager? {
        return firestoreManager.value
    }

    fun getStorageManager(): StorageManager? {
        return storageManager.value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeatherData() {
        // Check which location to use and retrieve the correct latitude and longitude
        val lat: Double
        val lon: Double

        if (_selectedLocation.value != null) {
            // If _selectedLocation is not null, use its latitude and longitude
            lat = _selectedLocation.value!!.latitude()
            lon = _selectedLocation.value!!.longitude()
        } else if (locationData.value != null) {
            // If locationData is not null, use its latitude and longitude
            lat = locationData.value!!.latitude
            lon = locationData.value!!.longitude
        } else {
            // If both are null, show permission dialog and exit the function
            _showPermissionDialog.value = true
            return
        }

        // Setup Retrofit to make the API call
        val url = "https://api.openweathermap.org/data/3.0/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitAPI = retrofit.create(RetrofitAPI::class.java)
        val apiKey = "a4a9525963331fc198bc03c89295e95e"

        // Call the API using the latitude and longitude
        val call = retrofitAPI.getData(lat, lon, apiKey)
        call.enqueue(object : Callback<OpenWeatherApi?> {
            override fun onResponse(
                call: Call<OpenWeatherApi?>,
                response: Response<OpenWeatherApi?>
            ) {
                val body = response.body()
                val weatherDescription = body?.current?.weather?.firstOrNull()?.description ?: "No data"
                currentWeather.value = weatherDescription.capitalize(Locale.getDefault())
            }

            override fun onFailure(call: Call<OpenWeatherApi?>, t: Throwable) {
                currentWeather.value = "Error: ${t.message}"
            }
        })
    }


    fun resetPermissionDialog() {
        _showPermissionDialog.value = false
    }
    fun searchForLocations(context: Context, query: String) {
        if (query.isEmpty()) {
            _searchResults.value = listOf()
            return
        }

        val client = MapboxGeocoding.builder()
            .accessToken(context.getString(R.string.mapbox_access_token))
            .query(query)
            .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
            .build()

        client.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                val points = response.body()?.features()?.mapNotNull { it.center() } ?: listOf()
                _searchResults.value = points
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                _searchResults.value = listOf()
            }
        })
    }

    fun selectLocation(point: Point?) {
        _selectedLocation.value = point
    }

    fun reverseGeocodeLocation(location: Point, context: Context, onAddressFound: (String) -> Unit) {
        val client = MapboxGeocoding.builder()
            .accessToken(context.getString(R.string.mapbox_access_token))
            .query(Point.fromLngLat(location.longitude(), location.latitude()))
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
            .build()

        client.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                val address = response.body()?.features()?.firstOrNull()?.placeName() ?: "Unknown location"
                onAddressFound(address)
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                onAddressFound("Error occurred: ${t.message}")
            }
        })
    }
}