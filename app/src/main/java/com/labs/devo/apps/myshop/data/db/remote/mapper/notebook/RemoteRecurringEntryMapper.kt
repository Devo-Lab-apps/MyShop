package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityRecurringEntry
import com.labs.devo.apps.myshop.data.models.notebook.RecurringEntry
import com.labs.devo.apps.myshop.view.util.EntityMapper

class RemoteRecurringEntryMapper : EntityMapper<RemoteEntityRecurringEntry, RecurringEntry> {
    fun entityListToPageList(entities: List<RemoteEntityRecurringEntry>): List<RecurringEntry> {
        val list: ArrayList<RecurringEntry> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun recurringEntryListToEntityList(entries: List<RecurringEntry>): List<RemoteEntityRecurringEntry> {
        val entities: ArrayList<RemoteEntityRecurringEntry> = ArrayList()
        for (entry in entries) {
            entities.add(mapToEntity(entry))
        }
        return entities
    }

    override fun mapFromEntity(entity: RemoteEntityRecurringEntry): RecurringEntry =
        RecurringEntry(
            recurringEntryId = entity.recurringEntryId,
            pageId = entity.pageId,
            name = entity.name,
            description = entity.description,
            frequency = entity.frequency,
            recurringTime = entity.recurringTime,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt
        )

    override fun mapToEntity(model: RecurringEntry): RemoteEntityRecurringEntry =
        RemoteEntityRecurringEntry(
            recurringEntryId = model.recurringEntryId,
            pageId = model.pageId,
            name = model.name,
            description = model.description,
            frequency = model.frequency,
            recurringTime = model.recurringTime,
            createdAt = model.createdAt,
            modifiedAt = model.modifiedAt
        )
}