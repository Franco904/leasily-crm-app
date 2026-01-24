package com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces

interface SharedStorageDataSource {
    suspend fun getFileName(contentUriString: String): String
}
