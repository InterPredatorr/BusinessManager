@file:JvmName("AppModuleKkJvm")
package app.di

import app.business.manager.database.UserDatabase
import app.data.cache.CacheService
import app.data.local.UserDatabaseDriverFactory
import app.data.user.UserCacheService
import app.data.user.UserEntity
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module


actual val platformAppModule: Module = module {
    single<CacheService<UserEntity>>(named(userCacheServiceKey)) {
        val db = UserDatabase(UserDatabaseDriverFactory(get()).createDriver())
        UserCacheService(db)
    }
}