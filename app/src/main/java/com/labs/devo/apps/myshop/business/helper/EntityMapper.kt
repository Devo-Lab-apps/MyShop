package com.labs.devo.apps.myshop.business.helper

interface EntityMapper <Entity, Model>{

    fun mapFromEntity(entity: Entity): Model

    fun mapToEntity(model: Model): Entity
}