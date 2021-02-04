package com.labs.devo.apps.myshop.business.helper

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

val user = "user"
val account = "account"
val notebook = "notebook"
val page = "page"


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

    fun getNotebookReference(accountId: String): DocumentReference {
        return getAccountDocumentReference(accountId)
            .collection(notebook).document()
    }


    fun getPageCollection(): CollectionReference {
        return db.collection(page)
    }

    fun getPageReference(pageId: String): DocumentReference {
        return db.collection(page).document(pageId)
    }

    fun getPageReference(): DocumentReference {
        return db.collection(page).document()
    }

    suspend fun runWriteBatch(f: () -> Unit) {
        db.runBatch {
            f.invoke()
        }.await()
    }

    suspend fun runUpdateBatch(f: () -> Unit) {
        db.runTransaction {
            f.invoke()
        }.await()
    }
}



