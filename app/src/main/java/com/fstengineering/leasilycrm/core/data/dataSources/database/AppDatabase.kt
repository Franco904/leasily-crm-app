package com.fstengineering.leasilycrm.core.data.dataSources.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fstengineering.leasilycrm.core.data.dataSources.database.converters.LocalDateTimeTypeConverter
import com.fstengineering.leasilycrm.core.data.dataSources.database.converters.LocalDateTypeConverter
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSearchedNeighborhoodDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertySeaProximityTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantContactOriginTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.Assistance
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRef
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRef
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegment
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentType
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.Property
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRef
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFile
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPrice
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyType
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.Stay
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFile
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransfer
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.Tenant
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginType

@Database(
    /*
     * Domain tables:
     *
     * AssistanceCharacteristic, CustomerSegmentType, PropertyCharacteristic, PropertySeaProximityType,
     * PropertyType and TenantContactOriginType.
     */
    entities = [
        Assistance::class,
        AssistanceCharacteristic::class,
        AssistanceCharacteristicAssistanceCrossRef::class,
        AssistanceInteraction::class,
        AssistanceSearchedNeighborhood::class,
        AssistanceSentPropertyCrossRef::class,
        CustomerSegment::class,
        CustomerSegmentType::class,
        Property::class,
        PropertyCharacteristic::class,
        PropertyCharacteristicPropertyCrossRef::class,
        PropertyFile::class,
        PropertyOwner::class,
        PropertyOwnerFile::class,
        PropertyPrice::class,
        PropertySeaProximityType::class,
        PropertyType::class,
        Stay::class,
        StayFile::class,
        StayTransfer::class,
        Tenant::class,
        TenantContactOriginType::class,
    ],
    version = AppDatabase.DB_VERSION,
)
@TypeConverters(
    value = [
        LocalDateTimeTypeConverter::class,
        LocalDateTypeConverter::class,
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assistanceDao(): AssistanceDao

    abstract fun assistanceCharacteristicAssistanceCrossRefDao(): AssistanceCharacteristicAssistanceCrossRefDao

    abstract fun assistanceCharacteristicDao(): AssistanceCharacteristicDao

    abstract fun assistanceInteractionDao(): AssistanceInteractionDao

    abstract fun assistanceSearchedNeighborhoodDao(): AssistanceSearchedNeighborhoodDao

    abstract fun assistanceSentPropertyCrossRefDao(): AssistanceSentPropertyCrossRefDao

    abstract fun customerSegmentDao(): CustomerSegmentDao

    abstract fun customerSegmentTypeDao(): CustomerSegmentTypeDao

    abstract fun propertyDao(): PropertyDao

    abstract fun propertyCharacteristicPropertyCrossRefDao(): PropertyCharacteristicPropertyCrossRefDao

    abstract fun propertyCharacteristicDao(): PropertyCharacteristicDao

    abstract fun propertyFileDao(): PropertyFileDao

    abstract fun propertyOwnerDao(): PropertyOwnerDao

    abstract fun propertyOwnerFileDao(): PropertyOwnerFileDao

    abstract fun propertyPriceDao(): PropertyPriceDao

    abstract fun propertySeaProximityTypeDao(): PropertySeaProximityTypeDao

    abstract fun propertyTypeDao(): PropertyTypeDao

    abstract fun stayDao(): StayDao

    abstract fun stayFileDao(): StayFileDao

    abstract fun stayTransferDao(): StayTransferDao

    abstract fun tenantDao(): TenantDao

    abstract fun tenantContactOriginTypeDao(): TenantContactOriginTypeDao

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "leasily_app_db"

        fun buildDatabase(appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                context = appContext,
                klass = AppDatabase::class.java,
                name = DB_NAME,
            )
                .build()
        }

        fun buildInMemoryDatabase(appContext: Context): AppDatabase {
            return Room.inMemoryDatabaseBuilder(
                context = appContext,
                klass = AppDatabase::class.java,
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
