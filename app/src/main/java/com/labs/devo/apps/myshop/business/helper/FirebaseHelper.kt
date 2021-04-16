package com.labs.devo.apps.myshop.business.helper

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.account
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.entry
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.microEntry
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.notebook
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.page
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.recurringEntry
import com.labs.devo.apps.myshop.business.helper.FirebaseConstants.user
import kotlinx.coroutines.tasks.await


object FirebaseConstants {
    const val user = "user"
    const val account = "account"
    const val notebook = "notebook"
    const val page = "page"
    const val entry = "entry"
    const val recurringEntry = "recurring_entry"
    const val microEntry = "micro_entry"
    const val foreignNotebookName = "Foreign"
    const val foreignNotebookKey = "foreign"
}


object FirebaseHelper {
    //initialize db
    private val db = FirebaseFirestore.getInstance()

    private fun getUsersCollection(): CollectionReference {
        return db.collection(user)
    }

    fun getUsersDocReference(userId: String): DocumentReference {
        return getUsersCollection().document(userId)
    }

    private fun getAccountCollection(): CollectionReference {
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

    fun getEntryCollection(pageId: String): CollectionReference {
        return getPageCollection().document(pageId).collection(entry)
    }

    fun getEntryReference(pageId: String, entryId: String): DocumentReference {
        return getEntryCollection(pageId).document(entryId)
    }

    fun getEntryReference(pageId: String): DocumentReference {
        return getEntryCollection(pageId).document()
    }

    fun getRecurringEntryCollection(accountId: String, pageId: String): CollectionReference {
        return getAccountCollection().document(accountId).collection(recurringEntry)
    }

    fun getRecurringEntryReference(accountId: String, pageId: String, recurringRecurringEntryId: String): DocumentReference {
        return getRecurringEntryCollection(accountId, pageId).document(recurringRecurringEntryId)
    }

    fun getRecurringEntryReference(accountId: String, pageId: String): DocumentReference {
        return getRecurringEntryCollection(accountId, pageId).document()
    }

    fun getMicroEntryCollection(pageId: String, recurringEntryId: String): CollectionReference {
        return getRecurringEntryReference(pageId, recurringEntryId).collection(microEntry)
    }

    fun getMicroEntryReference(pageId: String, recurringEntryId: String, amount: String): DocumentReference {
        return getMicroEntryCollection(pageId, recurringEntryId).document(amount)
    }

    fun getMicroEntryReference(pageId: String, recurringEntryId: String): DocumentReference {
        return getMicroEntryCollection(pageId, recurringEntryId).document()
    }

    suspend fun runWriteBatch(f: () -> Unit) {
        db.runBatch {
            f.invoke()
        }.await()
    }

    suspend fun runTransaction(f: (transaction: Transaction) -> Unit) {
        db.runTransaction {
            f.invoke(it)
        }.await()
    }

}



