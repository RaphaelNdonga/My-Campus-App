package com.mycampusapp.imageresource

import android.content.ContentResolver
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.DataStatus
import com.mycampusapp.data.DocumentData
import com.mycampusapp.util.COURSE_ID
import com.mycampusapp.util.IMAGES
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val storageReference: StorageReference,
    sharedPreferences: SharedPreferences,
    courseCollection: CollectionReference,
    private val contentResolver: ContentResolver
) : ViewModel() {
    private val courseId = sharedPreferences.getString(COURSE_ID, "")!!
    private val imagesCollection = courseCollection.document(courseId).collection(IMAGES)
    private val _images = MutableLiveData<List<DocumentData>>()
    val images: LiveData<List<DocumentData>> = _images
    private val _status = MutableLiveData<DataStatus>(DataStatus.LOADING)
    val status: LiveData<DataStatus> = _status

    fun getImagesRef(): StorageReference {
        return storageReference.child(IMAGES)
    }

    fun addFirestoreData(documentData: DocumentData) {
        imagesCollection.document(documentData.id).set(documentData)
    }

    fun addSnapshotListener(): ListenerRegistration {
        return imagesCollection.addSnapshotListener { querySnapshot, firestoreException ->
            _images.value = querySnapshot?.toObjects(DocumentData::class.java)
            if (firestoreException != null) {
                Timber.i("The firebase exception is $firestoreException")
            }
            checkDataStatus()
        }
    }

    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun checkDataStatus() {
        try {
            val imagesList = _images.value
            if (imagesList.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        } catch (npe: NullPointerException) {
            _status.value = DataStatus.EMPTY
        }
    }

    fun writeDataToFile(inputStream: InputStream?, imageFile: File) {
        val fos = FileOutputStream(imageFile)
        fos.write(inputStream?.readBytes())
        fos.flush()
        fos.close()
    }
}