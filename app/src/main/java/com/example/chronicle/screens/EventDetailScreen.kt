package com.example.chronicle.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HideSource
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.chronicle.navigation.Routes
import com.example.chronicle.viewmodel.NavigationViewModel

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer, Color.White )
    val titleText = rememberSaveable { mutableStateOf("") }
    val bodyText = rememberSaveable { mutableStateOf("") }
    val currentImage = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val selectedEvent = navViewModel.selectedEvent.value
    val deleteButtonColor = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(gradientColors))
    ) {

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
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 70.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = navViewModel.selectedEvent.value.date,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = selectedEvent.weather,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = selectedEvent.tag,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(
                        text = selectedEvent.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                enabled = false,
                maxLines = 1,
                value = titleText.value,
                onValueChange = { titleText.value = it },
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    placeholder = {
                        Column {
                            Text(
                                text = selectedEvent.body,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                    },
                    enabled = false,
                    maxLines = 20,
                    value = bodyText.value,
                    onValueChange = { bodyText.value = it },
                    textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface
                    )
                )

                Column {
                    if (selectedEvent.images.isNotEmpty()) {
                        val pagerState = rememberPagerState(pageCount = {
                            selectedEvent.images.size
                        })
                        HorizontalPager(
                            state = pagerState,
                        ) {page ->
                            currentImage.value = selectedEvent.images.get(pagerState.currentPage).toString()
                            SubcomposeAsyncImage(
                                modifier = Modifier.fillMaxWidth()
                                    .height(350.dp),
                                model = currentImage.value,
                                loading = {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }

                                },
                                contentDescription = null,
                            )
                        }

                        Spacer(modifier = Modifier.height(20 .dp))

                        Row(
                            Modifier
                                .wrapContentHeight()
                                .fillMaxSize()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            repeat(pagerState.pageCount) { iteration ->
                                val color =
                                    if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.secondaryContainer
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30 .dp))

            }

            Spacer(modifier = Modifier.height(4 .dp))

            if (navViewModel.selectedEvent.value.userId == navViewModel.getFirebaseAuthManager()
                    ?.getFirebaseAuth()?.uid
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Text(
                            color = MaterialTheme.colorScheme.secondary,
                            text = selectedEvent.location,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        Icon(
                            imageVector = if (selectedEvent.isPublic) Icons.Outlined.Public else Icons.Outlined.HideSource,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Text(
                            color = MaterialTheme.colorScheme.secondary,
                            text = if (selectedEvent.isPublic) "Public" else "Private",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        navViewModel.selectedEvent.value = selectedEvent
                        navController.navigate(Routes.EditEvent.value) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                        Text(
                            text = "Edit",
                            color = MaterialTheme.colorScheme.primary
                        ) }
                }

                Spacer(modifier = Modifier.height(8.dp))
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = deleteButtonColor,
                    onClick = {
                        navViewModel.getFirestoreManager()?.deleteEvent(selectedEvent) {
                            Toast.makeText(
                                context,
                                "Event deleted",
                                Toast.LENGTH_SHORT,
                            ).show()
                            navController.popBackStack()
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                        Text(
                            text = "Delete",
                        ) }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventDetailScreenPreview() {
    EventDetailScreen(navController = NavController(LocalContext.current), navViewModel = NavigationViewModel())
}