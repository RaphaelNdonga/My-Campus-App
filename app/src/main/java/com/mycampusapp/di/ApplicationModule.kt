package com.mycampusapp.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import com.mycampusapp.util.sharedPrefFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
    }
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context):ContentResolver{
        return context.contentResolver
    }
}