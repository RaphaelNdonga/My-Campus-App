package com.mycampusapp.resources

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mycampusapp.util.COURSE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun getImagesRef():StorageReference {
        val courseId = sharedPreferences.getString(COURSE_ID,"")!!
        return firebaseStorage.reference.child(courseId)
    }

}