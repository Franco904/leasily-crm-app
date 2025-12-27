package com.fstengineering.leasilycrm.core.data.dataSources.cache.interfaces

interface CacheLocalDataSource {
    fun <T> get(key: String): T?

    fun <T> put(key: String, value: T)

    fun <T> removeWhere(predicate: (T?) -> Boolean)
}
