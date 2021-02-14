package com.labs.devo.apps.myshop.data.db.local.mapper.notebook

import com.labs.devo.apps.myshop.business.helper.EntityMapper
import com.labs.devo.apps.myshop.data.db.local.models.notebook.LocalEntityEntry
import com.labs.devo.apps.myshop.data.models.notebook.Entry

class LocalEntryMapper : EntityMapper<LocalEntityEntry, Entry> {

    fun entityListToPageList(entities: List<LocalEntityEntry>): List<Entry> {
        val list: ArrayList<Entry> = ArrayList()
        for (entity in entities) {
            list.add(mapFromEntity(entity))
        }
        return list
    }

    fun pageListToEntityList(notebooks: List<Entry>): List<LocalEntityEntry> {
        val entities: ArrayList<LocalEntityEntry> = ArrayList()
        for (entry in notebooks) {
            entities.add(mapToEntity(entry))
        }
        return entities
    }

    override fun mapFromEntity(entity: LocalEntityEntry): Entry =
        Entry(
            entryId = entity.entryId,
            pageId = entity.pageId
        )

    override fun mapToEntity(model: Entry): LocalEntityEntry =
        LocalEntityEntry(
            entryId = model.entryId,
            pageId = model.pageId
        )

}