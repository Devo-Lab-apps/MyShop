package com.labs.devo.apps.myshop.data.db.remote.mapper.notebook

import com.labs.devo.apps.myshop.data.db.remote.models.notebook.RemoteEntityMicroEntry
import com.labs.devo.apps.myshop.data.models.notebook.MicroEntry
import com.labs.devo.apps.myshop.view.util.EntityMapper

class RemoteMicroEntryMapper : EntityMapper<RemoteEntityMicroEntry, MicroEntry> {
    fun entityListToPageList(entities: List<RemoteEntityMicroEntry>): List<MicroEntry> {
        val list: ArrayList<MicroEntry> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun recurringEntryListToEntityList(entries: List<MicroEntry>): List<RemoteEntityMicroEntry> {
        val entities: ArrayList<RemoteEntityMicroEntry> = ArrayList()
        for (entry in entries) {
            entities.add(mapToEntity(entry))
        }
        return entities
    }

    override fun mapFromEntity(entity: RemoteEntityMicroEntry): MicroEntry =
        MicroEntry(
            count = entity.count,
            amount = entity.amount,
            createdAt = entity.createdAt,
            pageId = entity.pageId,
            recurringEntryId = entity.recurringEntryId
        )

    override fun mapToEntity(model: MicroEntry): RemoteEntityMicroEntry =
        RemoteEntityMicroEntry(
            count = model.count,
            amount = model.amount,
            createdAt = model.createdAt,
            pageId = model.pageId,
            recurringEntryId = model.recurringEntryId
        )
}