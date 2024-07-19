package com.example.chronicle.components

import android.content.Context
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.chronicle.utils.LocationPermissionUtils
import com.example.chronicle.viewmodel.NavigationViewModel
import com.mapbox.geojson.Point

/**
 * encapsulated component (dialog) for displaying the map and location searching
 *
 * viewModel: NavigationViewModel
 * onDismissRequest: callback if user dismiss the dialog
 * onConfirmation: callback if user confirm the location
 * **/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapDialog(
    viewModel: NavigationViewModel,
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.observeAsState(listOf())
    val selectedLocation by viewModel.selectedLocation.observeAsState()
    val locationData by viewModel.locationData

    LaunchedEffect(LocationPermissionUtils) {
        LocationPermissionUtils.getLocationAndUpdateViewModel(context, viewModel)
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.isLocationEnabled.value) {
                    val displayLocation = selectedLocation ?: locationData?.let {
                        Point.fromLngLat(
                            it.longitude,
                            it.latitude
                        )
                    }
                    displayLocation?.let {
                        MapScreen(it)
                    }
                    TextField(
                        value = searchText,
                        onValueChange = { text ->
                            searchText = text
                            viewModel.searchForLocations(context, text)
                        },
                        placeholder = { Text("Search for a place...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    LocationSearchScreen(
                        viewModel,
                        searchResults,
                        { viewModel.selectLocation(it) },
                        context
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            displayLocation?.let { location ->
                                viewModel.reverseGeocodeLocation(location, context) { address ->
                                    onConfirmation(address)
                                    onDismissRequest()
                                }
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        enabled = displayLocation != null
                    ) {
                        Text("Confirm Location")
                    }
                } else {
                    Text(
                        "You need to enable location permissions in settings.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationSearchScreen(
    viewModel: NavigationViewModel,
    searchResults: List<Point>,
    onSelectLocation: (Point) -> Unit,
    context: Context
) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
    ) {
        LazyColumn {
            items(searchResults) { point ->
                var placeName by remember { mutableStateOf("Loading...") }

                LaunchedEffect(point) {
                    Location("").apply {
                        latitude = point.latitude()
                        longitude = point.longitude()
                    }

                    viewModel.reverseGeocodeLocation(point, context) { address ->
                        placeName = address
                    }
                }

                ListItem(
                    modifier = Modifier.clickable { onSelectLocation(point) },
                    headlineContent = { Text(placeName) }
                )
            }
        }
    }
}