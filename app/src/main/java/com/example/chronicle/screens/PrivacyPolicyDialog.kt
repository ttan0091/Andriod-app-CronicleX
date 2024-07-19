package com.example.chronicle.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyPolicyDialog(
    showDialog: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Privacy Policy") },
            text = {
                PrivacyPolicyContent()
            },
            confirmButton = {
                Button(onClick = { onAccept() }) {
                    Text("Agree")
                }
            },
            dismissButton = {
                Button(onClick = { onDecline() }) {
                    Text("Disagree")
                }
            },
            // adjust the dialog's size
            modifier = Modifier.width(400.dp).height(600.dp)
        )
    }
}
@Composable
fun PrivacyPolicyContent() {
    val state = rememberLazyListState()


    Row(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(state = state, modifier = Modifier.weight(1f).fillMaxWidth()) {
            item {
                Text("Chronicle X commits to protecting and respecting your privacy. This Privacy Policy explains how we collect, use, store, and disclose the information gathered through Chronicle X.")
                Text("Collection of Information", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            }
            item {
                Text("We may collect the following types of personal information:")
                Text("Basic Identity Information, such as your name, username, or email.")
                Text("Location Data, we may collect precise or imprecise location information from your device when you permit us to do so.")
                Text("The event content you've created, including textual information and images, will be securely saved in Firebase services.")
            }
            item {
                Text("Use of Information", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text("We use the information collected for the following purposes:")
                Text("To provide you with our services;")
                Text("To provide customer support;")
                Text("To notify you about important changes or updates to your account;")
            }
            item {
                Text("Sharing of Information", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text("We will not share your personal information with third parties except in the following circumstances:")
                Text("With your explicit consent;")
                Text("To comply with legal obligations or respond to court orders;")
            }
            item {
                Text("Security of Information", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text("We implement appropriate security measures to prevent your personal information from being accidentally lost, used, or accessed in an unauthorized way, altered, or disclosed. Access to your personal information is limited to those who have a business need to know it.")
            }
            item {
                Text("Your Rights", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text("Depending on where you reside, you are entitled to several rights concerning your personal information. These include accessing, correcting, or deleting the data we hold about you. You may also object to or request restrictions on the processing of your personal information and seek data portability.")
            }
            item {
                Text("Changes to the Privacy Policy", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text("We may update this Privacy Policy from time to time. We will notify you of any significant changes through appropriate online notices or through other methods provided by the Service.")
            }
            item {
                Text("Contact Us", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
                Text("If you have any questions or concerns about this Privacy Policy, please contact us at tta996842@gmail.com.")
            }
            item {
                Text("Last Updated: May 1, 2024.", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
            }

        }
    }
}

@Composable
@Preview
fun PrivacyPolicyDialogPreview() {
    PrivacyPolicyDialog(
        showDialog = true,
        onAccept = {},
        onDecline = {}
    )
}
