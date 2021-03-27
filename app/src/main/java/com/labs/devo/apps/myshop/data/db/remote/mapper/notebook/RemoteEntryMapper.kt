package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.view.util.EntityMapper
import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityEntry
import com.labs.devo.apps.myshop.data.models.notebook.Entry

class RemoteEntryMapper : EntityMapper<RemoteEntityEntry, Entry> {
    fun entityListToPageList(entities: List<RemoteEntityEntry>): List<Entry> {
        val list: ArrayList<Entry> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun pageListToEntityList(entries: List<Entry>): List<RemoteEntityEntry> {
        val entities: ArrayList<RemoteEntityEntry> = ArrayList()
        for (entry in entries) {
            entities.add(mapToEntity(entry))
        }
        return entities
    }

    override fun mapFromEntity(entity: RemoteEntityEntry): Entry =
        Entry(
            entryId = entity.entryId,
            pageId = entity.pageId,
            entryTitle = entity.entryTitle,
            entryAmount = entity.amount,
            entryDescription = entity.description,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )

    override fun mapToEntity(model: Entry): RemoteEntityEntry =
        RemoteEntityEntry(
            entryId = model.entryId,
            pageId = model.pageId,
            entryTitle = model.entryTitle,
            description = model.entryDescription,
            amount = model.entryAmount,
            createdAt = model.createdAt,
            modifiedAt = model.modifiedAt
        )
}