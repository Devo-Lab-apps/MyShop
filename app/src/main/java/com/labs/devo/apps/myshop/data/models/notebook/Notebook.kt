package com.labs.devo.apps.myshop.data.models.notebook



data class Notebook(

    val notebookId: String = "",

    val notebookName: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val modifiedAt: Long = System.currentTimeMillis()
)