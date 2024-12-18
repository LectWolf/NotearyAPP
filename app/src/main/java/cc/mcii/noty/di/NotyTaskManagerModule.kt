package cc.mcii.noty.di

import cc.mcii.noty.core.task.NotyTaskManager
import cc.mcii.noty.task.NotyTaskManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NotyTaskManagerModule {

    @Binds
    fun notyTaskManager(notyTaskManager: NotyTaskManagerImpl): NotyTaskManager
}
