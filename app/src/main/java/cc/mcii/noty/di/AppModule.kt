package cc.mcii.noty.di

import android.app.Application
import androidx.work.WorkManager
import cc.mcii.noty.core.preference.PreferenceManager
import cc.mcii.noty.preference.PreferenceManagerImpl
import cc.mcii.noty.preference.uiModePrefDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providePreferenceManager(application: Application): PreferenceManager {
        return PreferenceManagerImpl(application.uiModePrefDataStore)
    }

    @Singleton
    @Provides
    fun provideWorkManager(application: Application): WorkManager {
        return WorkManager.getInstance(application)
    }
}
