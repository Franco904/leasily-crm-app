package com.fstengineering.leasilycrm.core.infra.dataSources.inMemory

import androidx.collection.LruCache
import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces.InMemoryDataSource

class InMemoryDataSourceImpl : InMemoryDataSource {
    private val cache = LruCache<String, Any>(maxSize = 250)

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): T? = cache[key] as? T

    override fun <T> put(key: String, value: T) {
        cache.put(key, value as Any)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> removeWhere(predicate: (T?) -> Boolean) {
        cache.snapshot().forEach { (key, value) ->
            val canRemove = predicate(value as? T)
            if (canRemove) cache.remove(key)
        }
    }
}