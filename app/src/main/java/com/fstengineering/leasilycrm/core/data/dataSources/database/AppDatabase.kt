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
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertySeaProximityTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity

@Database(
    /*
     * Domain tables:
     *
     * assistance_characteristics, customer_segment_types, property_characteristics,
     * property_sea_proximity_types, property_types and tenant_contact_origin_types.
     */
    entities = [
        AssistanceEntity::class,
        AssistanceCharacteristicEntity::class,
        AssistanceCharacteristicAssistanceCrossRefEntity::class,
        AssistanceInteractionEntity::class,
        AssistanceSearchedNeighborhoodEntity::class,
        AssistanceSentPropertyCrossRefEntity::class,
        CustomerSegmentEntity::class,
        CustomerSegmentTypeEntity::class,
        PropertyEntity::class,
        PropertyCharacteristicEntity::class,
        PropertyCharacteristicPropertyCrossRefEntity::class,
        PropertyFileEntity::class,
        PropertyOwnerEntity::class,
        PropertyOwnerFileEntity::class,
        PropertyPriceEntity::class,
        PropertySeaProximityTypeEntity::class,
        PropertyTypeEntity::class,
        StayEntity::class,
        StayFileEntity::class,
        StayTransferEntity::class,
        TenantEntity::class,
        TenantContactOriginTypeEntity::class,
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
