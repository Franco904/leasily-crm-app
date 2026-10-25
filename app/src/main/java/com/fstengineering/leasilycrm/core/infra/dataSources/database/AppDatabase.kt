package com.fstengineering.leasilycrm.core.infra.dataSources.database

import android.content.Context
import androidx.room.Database
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fstengineering.leasilycrm.core.infra.dataSources.database.converters.AppDateTimeTypeConverter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.converters.AppDateTypeConverter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.converters.AppDecimalConverter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistancePreferredNeighborhoodDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.UserAnalyticsDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.LeadTenantDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ProprietarioDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ProprietarioFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.LeadTenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView

@Database(
    entities = [
        AssistanceEntity::class,
        AssistancePropertyCharacteristicCrossRefEntity::class,
        AssistanceInteractionEntity::class,
        AssistancePreferredNeighborhoodEntity::class,
        AssistanceSentPropertyCrossRefEntity::class,
        LeadTenantEntity::class,
        PropertyEntity::class,
        PropertyCharacteristicCrossRefEntity::class,
        PropertyFileEntity::class,
        ProprietarioOrm::class,
        ProprietarioFileOrm::class,
        PropertyPriceEntity::class,
        StayEntity::class,
        StayFileEntity::class,
        StayTransferEntity::class,
    ],
    views = [
        LeadTenantReadView::class,
    ],
    version = AppDatabase.DB_VERSION,
)
@TypeConverters(
    value = [
        AppDateTimeTypeConverter::class,
        AppDateTypeConverter::class,
        AppDecimalConverter::class,
    ]
)
@RewriteQueriesToDropUnusedColumns
abstract class AppDatabase : RoomDatabase() {
    abstract fun assistanceDao(): AssistanceDao

    abstract fun assistanceCharacteristicAssistanceCrossRefDao(): AssistanceCharacteristicAssistanceCrossRefDao

    abstract fun assistanceInteractionDao(): AssistanceInteractionDao

    abstract fun assistanceSearchedNeighborhoodDao(): AssistancePreferredNeighborhoodDao

    abstract fun assistanceSentPropertyCrossRefDao(): AssistanceSentPropertyCrossRefDao

    abstract fun userAnalyticsDao(): UserAnalyticsDao

    abstract fun leadTenantDao(): LeadTenantDao

    abstract fun propertyDao(): PropertyDao

    abstract fun propertyCharacteristicPropertyCrossRefDao(): PropertyCharacteristicPropertyCrossRefDao

    abstract fun propertyFileDao(): PropertyFileDao

    abstract fun proprietarioDao(): ProprietarioDao

    abstract fun proprietarioFileDao(): ProprietarioFileDao

    abstract fun propertyPriceDao(): PropertyPriceDao

    abstract fun stayDao(): StayDao

    abstract fun stayFileDao(): StayFileDao

    abstract fun stayTransferDao(): StayTransferDao

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
    }
}
