package com.mycampusapp.resources

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.ImageUrl
import com.mycampusapp.util.COURSES
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.IMAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageResourceViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences,
    private val courseCollection:CollectionReference
) : ViewModel() {
    private val courseId = sharedPreferences.getString(COURSE_ID, "")!!
    fun getImagesRef(): StorageReference {
        return firebaseStorage.reference.child(courseId).child(IMAGES)
    }

    fun addFirestoreData(uriString: String) {
        val imagesCollection = courseCollection.document(courseId).collection(IMAGES)
        val imageUrl = ImageUrl(imageUrl = uriString)
        imagesCollection.document(imageUrl.id).set(imageUrl)
    }

}