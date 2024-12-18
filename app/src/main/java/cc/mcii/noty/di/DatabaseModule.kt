package cc.mcii.noty.di

import android.app.Application
import cc.mcii.noty.data.local.NotyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(application: Application) = NotyDatabase.getInstance(application)

    @Singleton
    @Provides
    fun provideNotesDao(database: NotyDatabase) = database.getNotesDao()
}
