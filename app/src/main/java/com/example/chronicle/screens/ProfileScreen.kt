package com.example.chronicle.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.chronicle.R
import com.example.chronicle.components.AccountOptionCard
import com.example.chronicle.navigation.Routes
import com.example.chronicle.viewmodel.NavigationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(navController: NavController, navViewModel: NavigationViewModel) {
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    val context = LocalContext.current
    val username by remember {
        mutableStateOf(
            navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()?.currentUser?.displayName
        )
    }
    val photoUrl by remember {
        mutableStateOf(
            navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()?.currentUser?.photoUrl
        )
    }
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
                    .height(230.dp)
                    .clip(RoundedCornerShape(bottomStart = 200.dp))
                    .offset(x = 50.dp),
                onDraw = {
                    drawRect(Brush.linearGradient(gradientColors))
                })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .padding(top = 90.dp)
                    .padding(start = 10.dp)
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
                if (username != null) {
                    Text(
                        text = username!!,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }


                Text(
                    text = stringResource(R.string.wish_you_had_a_lovely_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
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
                AccountOptionCard(description = stringResource(R.string.reset_password), onClick = {
                    navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()
                        ?.sendPasswordResetEmail(
                            navViewModel.getFirebaseAuthManager()
                                ?.getFirebaseAuth()!!.currentUser?.email.toString()
                        )
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Reset password email sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }, icon = Icons.Filled.ChangeCircle)
                AccountOptionCard(description = stringResource(R.string.share), onClick = {
                    shareApp(context)
                }, icon = Icons.Filled.Share)
                AccountOptionCard(description = stringResource(R.string.setting), onClick = {
                    navController.navigate(Routes.Setting.value) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }, icon = Icons.Filled.Settings)
                AccountOptionCard(
                    description = stringResource(R.string.log_out),
                    onClick = {
                        navController.navigate(Routes.Login.value) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()?.signOut()
                    },
                    icon = Icons.AutoMirrored.Filled.ExitToApp
                )
            }
        }
    }
}

fun shareApp(context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Come to have a look this wonderful diary app! https://play.google.com/store/apps?hl=en&gl=US"
        )
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        navController = NavController(context = LocalContext.current),
        navViewModel = NavigationViewModel()
    )
}