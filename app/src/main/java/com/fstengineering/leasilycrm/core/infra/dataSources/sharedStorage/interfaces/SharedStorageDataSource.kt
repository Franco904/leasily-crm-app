package com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces

interface SharedStorageDataSource {
    suspend fun getFileName(contentUriString: String): String?
}
