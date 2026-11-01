# Refactor LegacyDatabase to AppDatabase

Refactor the Room database class from `LegacyDatabase` to `AppDatabase` while preserving all database functionality, migrations, entities (`PrivateContactEntity`, `TrashedContactEntity`), and DAOs (`PrivateContactDao`, `TrashedContactDao`).

## Proposed Changes

### Database Refactoring

#### [MODIFY] [LegacyDatabase.kt -> AppDatabase.kt](file:///C:/Users/ThePh/AndroidStudioProjects/ismart dialer/SmartDialer/app/src/main/java/dev/phoenix365/smartdialer/modal/db/AppDatabase.kt)
- Rename class `LegacyDatabase` to `AppDatabase`.
- Update file name from `LegacyDatabase.kt` to `AppDatabase.kt`.
- Keep all migrations (`MIGRATION_1_2`, `MIGRATION_2_3`, `MIGRATION_3_4`) and entities.

#### [MODIFY] [CommonModule.kt](file:///C:/Users/ThePh/AndroidStudioProjects/ismart dialer/SmartDialer/app/src/main/java/dev/phoenix365/smartdialer/CommonModule.kt)
- Update Room database builder to use `AppDatabase::class.java` and database name `"app_database"`.
- Update Koin definitions to provide DAOs from `AppDatabase`.

#### [MODIFY] [proguard-rules.pro](file:///C:/Users/ThePh/AndroidStudioProjects/ismart dialer/SmartDialer/app/proguard-rules.pro)
- Update ProGuard keep rules for `LegacyDatabase_Impl` to `AppDatabase_Impl`.

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to verify KSP code generation and compilation succeed with `AppDatabase`.
