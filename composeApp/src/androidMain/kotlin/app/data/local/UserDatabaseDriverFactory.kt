package app.data.local

import android.content.Context
import app.business.manager.database.UserDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class UserDatabaseDriverFactory(
    private val context: Context
) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(UserDatabase.Schema, context, "users.db")
    }
}

class AndroidUsersDatabaseFactory(private val driverFactory: UserDatabaseDriverFactory) {
    fun createDatabase(): UserDatabase {
        return UserDatabase(
            driver = driverFactory.createDriver()
        )
    }
}