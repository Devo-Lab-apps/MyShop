package com.labs.devo.apps.myshop.data.util

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

val users = "users"


object FirebaseUtil {
    val db = FirebaseFirestore.getInstance()

    fun getUsersCollection(): CollectionReference {
        return db.collection(users)
    }
}



