package dev.phoenix365.smartdialer

import androidx.room.Room
import dev.phoenix365.smartdialer.controller.CallAnalyticsViewModel
import dev.phoenix365.smartdialer.controller.CallLogViewModel
import dev.phoenix365.smartdialer.controller.CallNotificationManager
import dev.phoenix365.smartdialer.controller.CallStateManager
import dev.phoenix365.smartdialer.controller.CallViewModel
import dev.phoenix365.smartdialer.controller.ContactsViewModel
import dev.phoenix365.smartdialer.controller.GetCallerNameUseCase
import dev.phoenix365.smartdialer.controller.MainViewModel
import dev.phoenix365.smartdialer.controller.OpenSourcePurchaseHelper
import dev.phoenix365.smartdialer.controller.PurchaseHelper
import dev.phoenix365.smartdialer.controller.util.DefaultDispatcherProvider
import dev.phoenix365.smartdialer.controller.util.DispatcherProvider
import dev.phoenix365.smartdialer.controller.util.PreferenceManager
import dev.phoenix365.smartdialer.modal.db.AppDatabase
import dev.phoenix365.smartdialer.modal.`interface`.ICallLogRepository
import dev.phoenix365.smartdialer.modal.`interface`.ICallRepository
import dev.phoenix365.smartdialer.modal.`interface`.ICallerRepository
import dev.phoenix365.smartdialer.modal.`interface`.IContactsRepository
import dev.phoenix365.smartdialer.modal.repository.CallLogRepository
import dev.phoenix365.smartdialer.modal.repository.CallRepositoryImpl
import dev.phoenix365.smartdialer.modal.repository.CallerRepositoryImpl
import dev.phoenix365.smartdialer.modal.repository.ContactsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).addMigrations(AppDatabase.MIGRATION_1_2)
            .addMigrations(AppDatabase.MIGRATION_2_3)
            .addMigrations(AppDatabase.MIGRATION_3_4)
            .build()
    }
    single { get<AppDatabase>().privateContactDao() }
    single { get<AppDatabase>().trashedContactDao() }

    single<IContactsRepository> {
        ContactsRepository(androidContext(), get(), get())
    }
    single<ICallLogRepository> {
        CallLogRepository(androidContext(), androidContext().contentResolver, get(), get())
    }
    single {
        PreferenceManager(androidContext())
    }
    // Clean Architecture Wires
    single<ICallerRepository> { CallerRepositoryImpl(get()) }
    single { GetCallerNameUseCase(get()) }
    single { CallStateManager(get()) }
    single { CallNotificationManager(androidContext(), get()) }
    single { dev.phoenix365.smartdialer.controller.sensor.FlashNotificationManager(androidContext(), get()) }
    single<ICallRepository> { CallRepositoryImpl() }
    single<PurchaseHelper> { OpenSourcePurchaseHelper() }

    viewModel { ContactsViewModel(androidApplication(), get(), get(), get()) }
    viewModel { CallLogViewModel(androidApplication(), get(), androidContext().contentResolver, get()) }
    viewModel { CallViewModel(androidContext(), get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { CallAnalyticsViewModel(get(), get()) }
}

val appModule = module {
    includes(commonModule)
    single<PurchaseHelper> { OpenSourcePurchaseHelper() }
}