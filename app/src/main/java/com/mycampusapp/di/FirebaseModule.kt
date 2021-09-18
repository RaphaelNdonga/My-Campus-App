package com.mycampusapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.mycampusapp.util.COURSES
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.sharedPrefFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideAuthentication(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun provideFunctions(): FirebaseFunctions {
        return FirebaseFunctions.getInstance()
    }

    @Provides
    fun provideCoursesCollection(firestore: FirebaseFirestore): CollectionReference {
        return firestore.collection("courses")
    }

    @Provides
    fun provideCourseDocument(
        courseCollection: CollectionReference,
        sharedPreferences: SharedPreferences
    ): DocumentReference {
        val courseId = sharedPreferences.getString(COURSE_ID,"")!!
        return courseCollection.document(courseId)
    }

    @Provides
    fun provideCloudMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    fun provideCloudStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    fun provideStorageReference(
        firebaseStorage: FirebaseStorage,
        sharedPreferences: SharedPreferences
    ): StorageReference {
        val courseId = sharedPreferences.getString(COURSE_ID, "")!!
        return firebaseStorage.reference.child(courseId)
    }
}