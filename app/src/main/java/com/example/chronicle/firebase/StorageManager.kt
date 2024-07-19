package com.example.chronicle.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

/**
 * a manager provide firebase storage methods - responsible for File storage (images in our case)
 *
 * firestore is not capable of storing large amount/size files so we have to use firebase storage
 * **/
class StorageManager {
    private var storage = Firebase.storage
    private var storageRef = storage.reference

    /**
     * the method to upload the images to firebase storage
     *
     * it first put the file to the firebase storage, and then retrieve the downloadUrl for uploaded
     * images.
     * the downloadUrl will then be stored in the event document image field to firestore.
     * **/
    fun uploadImages(uri: Uri, callBack: (downloadUrl: String) -> Unit) {
        val imageName = "image_${UUID.randomUUID()}"
        val imgRef = storageRef.child("images/$imageName")

        imgRef.putFile(uri)
            .addOnSuccessListener { _ ->
                Log.d("putFile", "Image uploaded successfully")
                imgRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        callBack(uri.toString())
                    }
                    .addOnFailureListener { e ->
                        callBack(e.toString())
                    }

            }
            .addOnFailureListener {
                Log.d("putFile", "$it")
            }
    }
}