package com.labs.devo.apps.myshop.data.db.remote.mapper.item

import com.labs.devo.apps.myshop.data.db.remote.models.item.RemoteEntityItem
import com.labs.devo.apps.myshop.data.db.remote.models.item.RemoteEntityItemDetail
import com.labs.devo.apps.myshop.data.models.item.Item
import com.labs.devo.apps.myshop.data.models.item.ItemDetail
import com.labs.devo.apps.myshop.view.util.EntityMapper

class RemoteItemDetailMapper: EntityMapper<RemoteEntityItemDetail, ItemDetail> {
    override fun mapFromEntity(entity: RemoteEntityItemDetail): ItemDetail {
        return ItemDetail(
            entity.itemDetailId,
            entity.itemId,
            entity.itemName,
            entity.quantity,
            entity.description,
            entity.sortValue,
            entity.category,
            entity.subCategory,
            entity.boughtFrom,
            entity.imageUrl,
            entity.metadata,
            entity.tags,
            entity.expiresAt,
            entity.boughtAt,
            entity.createdAt,
            entity.modifiedAt
        )
    }

    override fun mapToEntity(model: ItemDetail): RemoteEntityItemDetail {
        return RemoteEntityItemDetail(
            model.itemDetailId,
            model.itemId,
            model.itemName,
            model.quantity,
            model.description,
            model.sortValue,
            model.category,
            model.subCategory,
            model.boughtFrom,
            model.imageUrl,
            model.metadata,
            model.tags,
            model.expiresAt,
            model.boughtAt,
            model.createdAt,
            model.modifiedAt
        )
    }
}