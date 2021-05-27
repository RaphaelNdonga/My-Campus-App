package com.mycampusapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Provides
    fun provideAuthentication():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Provides
    fun provideFunctions(): FirebaseFunctions{
        return FirebaseFunctions.getInstance()
    }
    @Provides
    fun provideCoursesCollection(firestore: FirebaseFirestore):CollectionReference{
        return firestore.collection("courses")
    }
    @Provides
    fun provideCloudMessaging():FirebaseMessaging{
        return FirebaseMessaging.getInstance()
    }
}