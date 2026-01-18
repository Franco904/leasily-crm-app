package com.fstengineering.leasilycrm.core.data.dataSources.inMemory.interfaces

interface InMemoryDataSource {
    fun <T> get(key: String): T?

    fun <T> put(key: String, value: T)

    fun <T> removeWhere(predicate: (T?) -> Boolean)
}
