package com.fstengineering.leasilycrm.core.infra.inMemory.interfaces

interface InMemoryStorage {
    fun <T> get(key: String): T?

    fun <T> put(key: String, value: T)

    fun <T> removeWhere(predicate: (T?) -> Boolean)
}
