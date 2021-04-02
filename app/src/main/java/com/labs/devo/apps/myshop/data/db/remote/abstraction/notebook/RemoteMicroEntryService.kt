package com.labs.devo.apps.myshop.data.db.remote.abstraction.notebook

import com.labs.devo.apps.myshop.data.models.notebook.MicroEntry

interface RemoteMicroEntryService {
    suspend fun getMicroEntries(
        pageId: String,
        recurringEntryId: String
    ): List<MicroEntry>

    suspend fun insertMicroEntries(microEntries: List<MicroEntry>): List<MicroEntry>

    suspend fun insertMicroEntry(microEntry: MicroEntry): MicroEntry

    suspend fun updateMicroEntries(microEntries: List<MicroEntry>): List<MicroEntry>

    suspend fun updateMicroEntry(createdAt: Long, microEntry: MicroEntry): MicroEntry

    suspend fun deleteMicroEntry(createdAt: Long, microEntry: MicroEntry)

    suspend fun deleteMicroEntries(microEntries: List<MicroEntry>)
}