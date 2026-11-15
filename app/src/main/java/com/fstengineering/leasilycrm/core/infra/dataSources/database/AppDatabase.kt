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
import com.fstengineering.leasilycrm.imoveis.infra.dataSources.ImoveisReadDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistancePreferredNeighborhoodDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceSentImovelCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.UserAnalyticsDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.LeadTenantDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelCharacteristicImovelCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelPriceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceImovelCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.LeadTenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources.ProprietarioReadDataSource as ImovelDashboardProprietarioReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ProprietarioReadDataSource as ImovelRegistrationProprietarioReadDataSource
import com.fstengineering.leasilycrm.proprietarioDetails.infra.dataSources.ProprietarioDetailsReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioRegistrationReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarios.infra.dataSources.ProprietariosReadDataSource

@Database(
    entities = [
        AssistanceEntity::class,
        AssistanceImovelCharacteristicCrossRefEntity::class,
        AssistanceInteractionEntity::class,
        AssistancePreferredNeighborhoodEntity::class,
        AssistanceSentImovelCrossRefEntity::class,
        LeadTenantEntity::class,
        ImovelOrm::class,
        ImovelCharacteristicCrossRefOrm::class,
        ImovelFileOrm::class,
        ProprietarioOrm::class,
        ProprietarioFileOrm::class,
        ImovelPriceOrm::class,
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

    abstract fun assistanceSentImovelCrossRefDao(): AssistanceSentImovelCrossRefDao

    abstract fun userAnalyticsDao(): UserAnalyticsDao

    abstract fun leadTenantDao(): LeadTenantDao

    abstract fun imovelDao(): ImovelDao

    abstract fun imovelCharacteristicImovelCrossRefDao(): ImovelCharacteristicImovelCrossRefDao

    abstract fun imovelFileDao(): ImovelFileDao

    abstract fun proprietarioWriteDataSource(): ProprietarioWriteDataSource

    abstract fun imovelWriteDataSource(): ImovelWriteDataSource

    abstract fun imovelSharedWriteDataSource(): ImovelSharedWriteDataSource
    abstract fun imoveisReadDataSource(): ImoveisReadDataSource


    abstract fun proprietarioRegistrationReadDataSource(): ProprietarioRegistrationReadDataSource

    abstract fun proprietarioDetailsReadDataSource(): ProprietarioDetailsReadDataSource

    abstract fun proprietariosReadDataSource(): ProprietariosReadDataSource

    abstract fun imovelRegistrationProprietarioReadDataSource(): ImovelRegistrationProprietarioReadDataSource

    abstract fun imovelDashboardProprietarioReadDataSource(): ImovelDashboardProprietarioReadDataSource

    abstract fun imovelPriceDao(): ImovelPriceDao

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
