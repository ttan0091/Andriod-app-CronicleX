package com.example.chronicle.utils

/**
Used to validate a string is email or not e.g. aString.isValidEmail()
 **/
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    return matches(emailRegex.toRegex())
}