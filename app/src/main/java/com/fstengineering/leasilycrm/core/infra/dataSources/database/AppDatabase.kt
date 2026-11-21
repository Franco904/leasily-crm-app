package com.fstengineering.leasilycrm.core.infra.dataSources.database

import android.content.Context
import androidx.room.Database
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoRegistrationReadDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoWriteDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.ImovelReadDataSource as AtendimentoRegistrationImovelReadDataSource
import com.fstengineering.leasilycrm.clienteDetails.infra.dataSources.ClienteDetailsReadDataSource
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteRegistrationReadDataSource
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clientes.infra.dataSources.ClientesReadDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.converters.AppDateTimeTypeConverter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.converters.AppDateTypeConverter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.converters.AppDecimalConverter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.UserAnalyticsDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoPreferredNeighborhoodOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelEnviadoCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ClienteOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.ClienteView
import com.fstengineering.leasilycrm.imoveis.infra.dataSources.ImoveisReadDataSource
import com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources.ImovelDashboardReadDataSource
import com.fstengineering.leasilycrm.imovelDetails.infra.dataSources.ImovelDetailsReadDataSource
import com.fstengineering.leasilycrm.imovelOcupacao.infra.dataSources.ImovelOcupacaoReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelRegistrationReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.proprietarioDetails.infra.dataSources.ProprietarioDetailsReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioRegistrationReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarios.infra.dataSources.ProprietariosReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ProprietarioReadDataSource as ImovelRegistrationProprietarioReadDataSource
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.dataSources.ClienteReadDataSource as OccupiedPeriodDetailsClienteReadDataSource
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.dataSources.ClienteReadDataSource as OccupiedPeriodRegistrationClienteReadDataSource

@Database(
    entities = [
        AtendimentoOrm::class,
        AtendimentoImovelCharacteristicCrossRefOrm::class,
        InteracaoOrm::class,
        AtendimentoPreferredNeighborhoodOrm::class,
        AtendimentoImovelEnviadoCrossRefOrm::class,
        ClienteOrm::class,
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
        ClienteView::class,
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
    abstract fun userAnalyticsDao(): UserAnalyticsDao

    abstract fun clienteSharedReadDataSource(): ClienteSharedReadDataSource

    abstract fun clienteWriteDataSource(): ClienteWriteDataSource

    abstract fun clienteRegistrationReadDataSource(): ClienteRegistrationReadDataSource

    abstract fun clientesReadDataSource(): ClientesReadDataSource

    abstract fun clienteDetailsReadDataSource(): ClienteDetailsReadDataSource

    abstract fun atendimentoWriteDataSource(): AtendimentoWriteDataSource

    abstract fun atendimentoSharedWriteDataSource(): AtendimentoSharedWriteDataSource

    abstract fun atendimentoRegistrationReadDataSource(): AtendimentoRegistrationReadDataSource

    abstract fun atendimentoRegistrationImovelReadDataSource(): AtendimentoRegistrationImovelReadDataSource

    abstract fun occupiedPeriodRegistrationClienteReadDataSource(): OccupiedPeriodRegistrationClienteReadDataSource

    abstract fun occupiedPeriodDetailsClienteReadDataSource(): OccupiedPeriodDetailsClienteReadDataSource

    abstract fun imovelDao(): ImovelDao



    abstract fun proprietarioWriteDataSource(): ProprietarioWriteDataSource

    abstract fun imovelWriteDataSource(): ImovelWriteDataSource

    abstract fun imovelSharedWriteDataSource(): ImovelSharedWriteDataSource

    abstract fun imoveisReadDataSource(): ImoveisReadDataSource

    abstract fun imovelDetailsReadDataSource(): ImovelDetailsReadDataSource

    abstract fun imovelDashboardReadDataSource(): ImovelDashboardReadDataSource

    abstract fun imovelOcupacaoReadDataSource(): ImovelOcupacaoReadDataSource

    abstract fun imovelRegistrationReadDataSource(): ImovelRegistrationReadDataSource

    abstract fun proprietarioRegistrationReadDataSource(): ProprietarioRegistrationReadDataSource

    abstract fun proprietarioDetailsReadDataSource(): ProprietarioDetailsReadDataSource

    abstract fun proprietariosReadDataSource(): ProprietariosReadDataSource

    abstract fun imovelRegistrationProprietarioReadDataSource(): ImovelRegistrationProprietarioReadDataSource

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
