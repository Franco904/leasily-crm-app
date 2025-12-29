package com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces

interface SharedStorage {
    suspend fun getFileName(contentUriString: String): String
}
