package com.example.android.mycampusapp.di

import android.content.Context
import androidx.room.Room
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDao
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): TimetableDatabase {
        return Room.databaseBuilder(appContext, TimetableDatabase::class.java, "timetable_db")
            .fallbackToDestructiveMigration().build()
    }
    @Provides
    fun provideDao(database: TimetableDatabase): TimetableDao {
        return database.timetableDao()
    }

}