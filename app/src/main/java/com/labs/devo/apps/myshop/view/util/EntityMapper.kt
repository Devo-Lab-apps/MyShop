package com.labs.devo.apps.myshop.view.util

interface EntityMapper <Entity, Model>{

    fun mapFromEntity(entity: Entity): Model

    fun mapToEntity(model: Model): Entity
}