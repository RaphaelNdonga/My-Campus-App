package com.mycampusapp.imageresource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.DocumentData
import com.mycampusapp.util.IMAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val storageReference: StorageReference,
    private val courseDocument: DocumentReference
) : ViewModel() {
    private val imagesCollection = courseDocument.collection(IMAGES)
    private val _images = MutableLiveData<List<DocumentData>>()
    val images: LiveData<List<DocumentData>> = _images

    fun addFirestoreData(image: DocumentData) {
        imagesCollection.document(image.id).set(image)
    }

    fun getImagesRef(): StorageReference {
        return storageReference.child(IMAGES)
    }
}