package com.mycampusapp.imageresource

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.ImageData
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.IMAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ImageResourceViewModel @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences,
    private val courseCollection:CollectionReference
) : ViewModel() {
    private val courseId = sharedPreferences.getString(COURSE_ID, "")!!
    private val imagesCollection = courseCollection.document(courseId).collection(IMAGES)
    private val _images = MutableLiveData<List<ImageData>>()
    val images:LiveData<List<ImageData>> = _images

    fun getImagesRef(): StorageReference {
        return firebaseStorage.reference.child(courseId).child(IMAGES)
    }

    fun addFirestoreData(uriString: String) {
        val imageUrl = ImageData(imageUrl = uriString)
        imagesCollection.document(imageUrl.id).set(imageUrl)
    }

    fun addSnapshotListener():ListenerRegistration{
        return imagesCollection.addSnapshotListener{ querySnapshot, firestoreException ->
            _images.value = querySnapshot?.toObjects(ImageData::class.java)
            if(firestoreException != null){
                Timber.i("The firebase exception is $firestoreException")
            }
        }
    }

}