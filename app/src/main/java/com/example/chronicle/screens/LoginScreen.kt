package com.example.chronicle.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.chronicle.R
import com.example.chronicle.navigation.Routes
import com.example.chronicle.utils.isValidEmail
import com.example.chronicle.viewmodel.NavigationViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController, navViewModel: NavigationViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val gradientColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        Color.White
    )
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(gradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .offset(y = (-100).dp),
                text = "Chronicle X",
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
                        text = stringResource(R.string.enter_email),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
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
                        text = stringResource(R.string.enter_password),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Go
                ),
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

            // Login and Register Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = {
                        navController.navigate(Routes.Registration.value) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please enter both email and password",
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else if (!email.isValidEmail()) {
                            Toast.makeText(
                                context,
                                "Invalid Email",
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            navViewModel.getFirebaseAuthManager()?.getFirebaseAuth()
                                ?.signInWithEmailAndPassword(email, password)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        navController.navigate(Routes.Calendar.value) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Wrong Email or Password",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                }
                        }
                    }
                ) { Text(text = stringResource(R.string.login)) }
            }

            Text(text = "OR")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(true)
                        .setServerClientId("935410862502-vislj929fdba0nbkr6uno5j6t8bo8pcr.apps.googleusercontent.com")
                        .build()

                    val request: GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    coroutineScope.launch {
                        try {
                            val result = credentialManager.getCredential(
                                request = request,
                                context = context,
                            )
                            handleSignIn(result, navViewModel, navController)
                        } catch (e: GetCredentialException) {
                            Log.e("GetCredentialException", "GetCredentialException", e)
                            // Make sign up request and call the API if there's no credential available
                            val googleIdOptionSignUp: GetGoogleIdOption =
                                GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId("935410862502-vislj929fdba0nbkr6uno5j6t8bo8pcr.apps.googleusercontent.com")
                                    .build()
                            val requestSignUp: GetCredentialRequest = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOptionSignUp)
                                .build()

                            try {
                                val result = credentialManager.getCredential(
                                    request = requestSignUp,
                                    context = context,
                                )
                                handleSignIn(result, navViewModel, navController)
                            } catch (e: GetCredentialException) {
                                Log.e("GetCredentialException2", "GetCredentialException", e)
                            }
                        }
                    }
                },
                /*TODO Fix this later*/
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium,
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sign_in_with_google),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun handleSignIn(
    result: GetCredentialResponse,
    navViewModel: NavigationViewModel,
    navController: NavController
) {
    // Handle the successfully returned credential.
    val credential = result.credential

    when (credential) {

        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    // Use google ID token exchange for Firebase credential
                    val googleIdToken = googleIdTokenCredential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                    navViewModel.getFirebaseAuthManager()?.signInWithCredential(
                        firebaseCredential
                    ) {
                        navController.navigate(Routes.Calendar.value) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    // Send googleIdTokenCredential to your server for validation and authentication
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("googleId", "Received an invalid google id token response", e)
                }
            } else {
                // Catch any unrecognized custom credential type here.
                Log.e("googleId", "Unexpected type of credential")
            }
        }

        else -> {
            // Catch any unrecognized credential type here.
            Log.e("googleId", "Unexpected type of credential")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = NavController(LocalContext.current),
        navViewModel = NavigationViewModel()
    )
}