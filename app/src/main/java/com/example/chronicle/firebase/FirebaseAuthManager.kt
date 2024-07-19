package com.example.chronicle.firebase

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

/**
 * a manager provide firebase authentication method and provide Firebase auth access
 *
 * it is used to provide firebase authentication method such as login/register, reset password etc.
 * **/
class FirebaseAuthManager {
    private var auth: FirebaseAuth = Firebase.auth

    /**
     * function to get the FirebaseAuth instance
     * **/
    fun getFirebaseAuth(): FirebaseAuth {
        return auth
    }

    /**
     * sign in with credentials, work with google credential to implement the google authentication
     * **/
    // Function to sign in with credentials
    fun signInWithCredential(firebaseCredential: AuthCredential, onSuccess: () -> Unit) {
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    onSuccess()
                    Log.d("signInWithCredential", "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signInWithCredential", "signInWithCredential:failure", task.exception)
                }
            }
    }

    /**
     * add username - used when user register account and provide the username
     * **/
    fun addUsername(username: String, onSuccess: () -> Unit) {
        val user = Firebase.auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
            }
    }

    /**
     * retrieve the user name and return empty string if the username is not available
     * **/
    fun getUserName(): String {
        return if (auth.currentUser?.displayName != null) {
            auth.currentUser?.displayName!!
        } else {
            ""
        }
    }
}