package app.presentation

import android.app.Application
import app.di.appModule
import app.presentation.main.AppInitializer
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}