package com.example.devmanagerweb.di

import com.example.devmanagerweb.data.repository.FakeRepositoryImpl
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDevManagerRepository(
        fakeRepositoryImpl: FakeRepositoryImpl
    ): DevManagerRepository
}
