package com.labs.devo.apps.myshop.view.util

data class QueryParams(
    var whereQuery: MutableMap<String, WhereClause> = mutableMapOf(),
    var orderBy: MutableMap<String, SORT_ORDER> = mutableMapOf(),
    var limit: Int = Int.MAX_VALUE,
    var offset: Int = 0
) {
    fun buildWhereQuery(): String {
        val list: MutableList<String> = mutableListOf()
        for (where in whereQuery) {
            var k = where.value.value
            when (where.value.type) {
                QUERY_TYPE.STRING -> {
                    k = "'$k'"
                }
                QUERY_TYPE.INT -> {

                }
                QUERY_TYPE.LONG -> {

                }
                QUERY_TYPE.DATE -> {

                }
            }
            list.add("${where.key} ${where.value.operation} $k")
        }
        return list.joinToString(" AND ")
    }

    fun buildOrderByQuery(): String {
        val list: MutableList<String> = mutableListOf()
        for (order in orderBy) {
            list.add("${order.key} ${order.value}")
        }
        return list.joinToString(",")
    }
}

data class WhereClause(
    val operation: String,
    val value: String,
    val type: QUERY_TYPE
)

enum class QUERY_TYPE {
    STRING,
    INT,
    LONG,
    DATE
}

enum class SORT_ORDER {
    ASC,
    DESC
}

object QueryHelper {
    fun handleOrderBy(colName: String, queryParams: QueryParams) {
        val orderBy = queryParams.orderBy
        if (colName in orderBy) {
            if (orderBy[colName] == SORT_ORDER.DESC)
                orderBy.remove(colName)
            else
                orderBy[colName] = SORT_ORDER.DESC
        } else {
            orderBy[colName] = SORT_ORDER.ASC
        }
    }
}