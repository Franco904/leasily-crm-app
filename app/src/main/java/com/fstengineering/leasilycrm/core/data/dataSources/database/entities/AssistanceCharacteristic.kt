package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistance_characteristics")
data class AssistanceCharacteristic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
)
