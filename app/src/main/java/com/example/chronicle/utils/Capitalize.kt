package com.example.chronicle.utils

import java.util.Locale

/**
    Capitalize is deprecated below as a solution to make the first letter uppercase.
    Call it directly from a string object.
 **/
fun String.titlecaseFirstCharIfItIsLowercase() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}