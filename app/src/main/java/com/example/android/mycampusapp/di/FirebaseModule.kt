package com.example.android.mycampusapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Singleton
    @Provides
    fun provideAuthentication():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Singleton
    @Provides
    fun provideFunctions(): FirebaseFunctions{
        return FirebaseFunctions.getInstance()
    }
    @Singleton
    @Provides
    fun provideCoursesCollection(firestore: FirebaseFirestore):CollectionReference{
        return firestore.collection("courses")
    }
    @Singleton
    @Provides
    fun provideCloudMessaging():FirebaseMessaging{
        return FirebaseMessaging.getInstance()
    }
}