package com.labs.devo.apps.myshop.data.db.remote.mapper.item

import com.labs.devo.apps.myshop.data.db.remote.models.item.RemoteEntityItem
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.view.util.EntityMapper

class RemoteItemMapper : EntityMapper<RemoteEntityItem, Item> {
    override fun mapFromEntity(entity: RemoteEntityItem): Item {
        return Item(
            entity.itemId,
            entity.itemName,
            entity.quantity,
            null,
            entity.createdAt,
            entity.modifiedAt
        )
    }

    override fun mapToEntity(model: Item): RemoteEntityItem {
        return RemoteEntityItem(
            model.itemId,
            model.itemName,
            model.quantity,
            model.imageUrl,
            model.createdAt,
            model.modifiedAt
        )
    }
}