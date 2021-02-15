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

    fun pageListToEntityList(notebooks: List<Entry>): List<RemoteEntityEntry> {
        val entities: ArrayList<RemoteEntityEntry> = ArrayList()
        for (notebook in notebooks) {
            entities.add(mapToEntity(notebook))
        }
        return entities
    }

    override fun mapFromEntity(entity: RemoteEntityEntry): Entry =
        Entry(
            entryId = entity.entryId,
            pageId = entity.pageId,
            entryTitle = entity.entryTitle,
            entryAmount = entity.amount
        )

    override fun mapToEntity(model: Entry): RemoteEntityEntry =
        RemoteEntityEntry(
            entryId = model.entryId,
            pageId = model.pageId,
            entryTitle = model.entryTitle,
            amount = model.entryAmount
        )
}