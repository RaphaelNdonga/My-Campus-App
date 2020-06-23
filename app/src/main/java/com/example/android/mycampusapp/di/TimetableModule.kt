package com.example.android.mycampusapp.di

import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class TimetableDatabase

@InstallIn(ApplicationComponent::class)
@Module
abstract class TimetableModule{
    @TimetableDatabase
    @Singleton
    @Binds
    abstract fun bindDatabaseTimetable(impl: TimetableLocalDataSource):TimetableDataSource
}