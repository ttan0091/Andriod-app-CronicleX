package com.example.chronicle.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Swipe
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.chronicle.data.Event
import com.example.chronicle.navigation.Routes
import com.example.chronicle.viewmodel.NavigationViewModel
import kotlinx.datetime.LocalDate
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        Color.White,
        Color.White
    )
    var publicEvents: List<Event> by remember { mutableStateOf(emptyList()) }
    var isFetchingData by remember { mutableStateOf(true) }
    var currentEvent: Event by remember {
        mutableStateOf(Event())
    }

    LaunchedEffect(navViewModel) {
        navViewModel.getFirestoreManager()?.getPublicEvent { events ->
            val sortedIndices = events.indices.sortedByDescending {
                LocalDate.parse(events[it].date)
            }
            // Update the state with the retrieved events
            publicEvents = sortedIndices.map { events[it] }
            isFetchingData = false
            Log.i("PUBLICEVENT", publicEvents.toString())
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
        if (publicEvents.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = {
                publicEvents.size
            })

            HorizontalPager(
                state = pagerState,
                contentPadding =
                if (publicEvents.size == 1){
                    PaddingValues(0.dp)
                } else {
                    when (pagerState.currentPage) {
                        0 -> {
                            PaddingValues(end = 32.dp)
                        }

                        publicEvents.size - 1 -> {
                            PaddingValues(start = 32.dp)
                        }

                        else -> {
                            PaddingValues(horizontal = 32.dp)
                        }
                    }
                }

            ) { page ->
                currentEvent = publicEvents.get(pagerState.currentPage)
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(gradientColors))
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState
                                    .currentPageOffsetFraction
                                ).absoluteValue

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 100.dp, bottom = 100.dp)
                                .clip(RoundedCornerShape(40.dp)),
                            colors = CardDefaults.elevatedCardColors(Color.White.copy(alpha = 1f))
                        ) {
                        }

                        Button(
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = 50.dp),
                            onClick = { /*TODO*/ },
                            enabled = false
                        ) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxSize(),
                                imageVector = Icons.Outlined.Swipe,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }


                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 200.dp),
                                text = currentEvent.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 5.dp),
                                text = currentEvent.date,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(30.dp))

                            ElevatedButton(
                                onClick = {
                                    navViewModel.selectedEvent.value = currentEvent
                                    Log.i("PUBLICEVENT2", currentEvent.toString())
                                    navController.navigate(Routes.EventDetail.value) {

                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }) {
                                Text(text = "View Detail")
                            }

                            Spacer(modifier = Modifier.height(60.dp))

                            Card(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(horizontal = 60.dp)
                                    .wrapContentHeight(),
                                colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(top = 10.dp, start = 10.dp)
                                        .fillMaxWidth(),
                                    text = currentEvent.body,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }

                    }
                }
            }
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 290.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.headlineSmall,
                    text = "There is no event so far",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
    EventScreen(
        navController = NavController(LocalContext.current),
        navViewModel = NavigationViewModel()
    )
}