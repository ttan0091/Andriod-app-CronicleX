package com.example.chronicle.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.chronicle.R
import com.example.chronicle.navigation.Routes
import com.example.chronicle.utils.isValidEmail
import com.example.chronicle.viewmodel.NavigationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrationScreen(navController: NavController, navViewModel: NavigationViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatpassword by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val gradientColors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer, Color.White )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Brush.linearGradient(gradientColors))
    ) {
        ElevatedButton(
            modifier = Modifier
                .offset(x = 10.dp, y = 10.dp),
            onClick = {
                navController.navigate(Routes.Login.value) {
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
                Icon(imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft, contentDescription = null)
                Text(text = "Back")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .offset(y = (-70).dp),
                text = "Register",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Enter Email",
                        color = MaterialTheme.colorScheme.secondary
                    ) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    keyboardController?.hide()
                })
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Enter Password",
                        color = MaterialTheme.colorScheme.secondary
                    ) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    keyboardController?.hide()
                }),
                trailingIcon = {
                    val img = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = img, null)
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = repeatpassword,
                onValueChange = { repeatpassword = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.confirm_password),
                        color = MaterialTheme.colorScheme.secondary
                    ) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    keyboardController?.hide()
                }),
                trailingIcon = {
                    val img = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = img, null)
                    }
                }
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                value = userName,
                onValueChange = { userName = it },
                label = {
                    Text(
                        text = "Enter Username (Optional)",
                        color = MaterialTheme.colorScheme.secondary
                    ) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    keyboardController?.hide()
                })
            )

            // Login and Register Button
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = {
                        if (email.isEmpty() || password.isEmpty() || repeatpassword.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please enter all the required fields",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        else if (password == repeatpassword) {
                            navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()?.createUserWithEmailAndPassword(email, password)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (userName!="") {
                                            navViewModel.getFirebaseAuthManager()?.addUsername(userName) {
                                                navController.navigate(Routes.Login.value) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                        else {
                                            navController.navigate(Routes.Login.value) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        if (!email.isValidEmail()) {
                                            Toast.makeText(
                                                context,
                                                "Invalid Email",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                        else if (password != repeatpassword) {
                                            Toast.makeText(
                                                context,
                                                "Password Not Match",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                        else if (password.length < 6) {
                                            Toast.makeText(
                                                context,
                                                "Password Too Short",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                        else {
                                            Toast.makeText(
                                                context,
                                                "Account Exist",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    }
                                }
                        }
                        else {
                            Toast.makeText(
                                context,
                                "Password not match",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                ) {
                    Text(
                        text = "Register",
                        color = MaterialTheme.colorScheme.primary
                    ) }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(navController = NavController(LocalContext.current), navViewModel = NavigationViewModel())
}