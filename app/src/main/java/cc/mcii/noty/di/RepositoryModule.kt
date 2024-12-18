package cc.mcii.noty.di

import cc.mcii.noty.core.repository.NotyNoteRepository
import cc.mcii.noty.repository.local.NotyLocalNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @LocalRepository
    fun notyLocalNoteRepository(localRepository: NotyLocalNoteRepository): NotyNoteRepository
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalRepository

