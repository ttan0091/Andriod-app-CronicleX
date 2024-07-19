package com.example.chronicle.firebase

import android.util.Log
import com.example.chronicle.data.Event
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

/**
 * a manager provide firestore methods - responsible for CRUD operation for event on cloud
 *
 * in general, we have a collection called `events` in firestore database, and inside collection
 * are documents each represent an event with fields/attributes that has the value of stored data.
 *
 * because the CRUD are async we used callback to ensure the operation is done and trigger other
 * operation accordingly.
 * **/
class FirestoreManager {
    private var db = Firebase.firestore

    /**
     * add a event
     * **/
    fun addEvent(event: Event, onSuccess: () -> Unit){
        val addedEvent = hashMapOf(
            "eventId" to "",
            "title" to event.title,
            "body" to event.body,
            "date" to event.date,
            "time" to event.time,
            "images" to event.images,
            "location" to event.location,
            "weather" to event.weather,
            "isPublic" to event.isPublic,
            "tag" to event.tag,
            "userId" to Firebase.auth.currentUser?.uid
        )

        db.collection("events")
            .add(addedEvent)
            .addOnSuccessListener {
                val eventId = it.id
                addedEvent["eventId"] = eventId
                it.update("eventId", eventId)
                    .addOnSuccessListener {
                        onSuccess()
                    }
            }
            .addOnFailureListener { e ->
                Log.w("ADDEVENT", "Error adding document", e)
            }
    }

    /**
     * edit a specified event
     * **/
    fun editEvent(event: Event, onSuccess: () -> Unit) {
        val editedEvent = hashMapOf(
            "eventId" to event.eventId,
            "title" to event.title,
            "body" to event.body,
            "date" to event.date,
            "time" to event.time,
            "images" to event.images,
            "location" to event.location,
            "weather" to event.weather,
            "isPublic" to event.isPublic,
            "tag" to event.tag,
            "userId" to Firebase.auth.currentUser?.uid
        )
        db.collection("events").document(event.eventId!!)
            .set(editedEvent)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.w("EDITEVENT", "Error editing document", e)
            }
    }

    /**
     * delete a specified event
     * **/
    fun deleteEvent(event: Event, onSuccess: () -> Unit) {
        db.collection("events").document(event.eventId!!)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
    }

    /**
     * get all the event for current user
     * **/
    fun getMyEvent(callBack: (List<Event>) -> Unit) {
        val events = mutableListOf<Event>()
        db.collection("events")
            .whereEqualTo("userId", Firebase.auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.toObject<Event>()
                    events.add(data)
                }
                callBack(events)
            }
            .addOnFailureListener { exception ->
                Log.w("GETEVENT", "Error getting documents.", exception)
                callBack(emptyList())
            }
    }

    /**
     * get all the public events, for the explore feature
     * **/
    fun getPublicEvent(callBack: (List<Event>) -> Unit) {
        val events = mutableListOf<Event>()
        db.collection("events")
            .whereEqualTo("isPublic", true)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.toObject<Event>()
                    events.add(data)
                }
                callBack(events)
            }
            .addOnFailureListener { exception ->
                Log.w("GETEVENT", "Error getting documents.", exception)
                callBack(emptyList())
            }
    }

    /**
     * get the events for current user on specified date
     * **/
    fun getMyEventOnDate(date: String, callBack: (List<Event>) -> Unit) {
        val events = mutableListOf<Event>()
        db.collection("events")
            .whereEqualTo("userId", Firebase.auth.currentUser?.uid)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.toObject<Event>()
                    events.add(data)
                }
                callBack(events)
            }
            .addOnFailureListener { exception ->
                Log.w("GETEVENT", "Error getting documents.", exception)
                callBack(emptyList())
            }
    }
}