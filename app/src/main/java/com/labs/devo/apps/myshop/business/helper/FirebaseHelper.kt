package com.labs.devo.apps.myshop.business.helper

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

val user = "user"
val account = "account"
val notebook = "notebook"


object FirebaseHelper {
    //initialize db
    private val db = FirebaseFirestore.getInstance()

    fun getUsersCollection(): CollectionReference {
        return db.collection(user)
    }

    fun getUsersDocReference(userId: String): DocumentReference {
        return getUsersCollection().document(userId)
    }

    fun getAccountCollection(): CollectionReference {
        return db.collection(account)
    }

    fun getAccountDocumentReference(accountId: String): DocumentReference {
        return getAccountCollection().document(accountId)
    }

    fun getAccountDocumentReference(): DocumentReference {
        return getAccountCollection().document()
    }

    fun getNotebookCollection(email: String): CollectionReference {
        return getAccountDocumentReference(email)
            .collection(notebook)
    }

    fun getNotebookReference(email: String, notebookId: String): DocumentReference {
        return getAccountDocumentReference(email)
            .collection(notebook).document(notebookId)
    }

    fun getNotebookReference(email: String): DocumentReference {
        return getAccountDocumentReference(email)
            .collection(notebook).document()
    }
}



