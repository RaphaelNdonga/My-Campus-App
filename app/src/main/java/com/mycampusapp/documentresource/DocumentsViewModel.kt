package com.mycampusapp.documentresource

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.DataStatus
import com.mycampusapp.data.DocumentData
import com.mycampusapp.util.DOCUMENTS
import com.mycampusapp.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val storageReference: StorageReference,
    private val contentResolver: ContentResolver,
    courseDocument: DocumentReference,
    private val storageDirectory: File?
) : ViewModel() {
    private val _documentList = MutableLiveData<List<DocumentData>>()
    val documentList: LiveData<List<DocumentData>> = _documentList
    private val documentsCollection = courseDocument.collection(DOCUMENTS)

    private val _status = MutableLiveData<DataStatus>(DataStatus.LOADING)
    val status: LiveData<DataStatus> = _status

    private val _toaster = MutableLiveData<Event<Unit>>()
    val toaster: LiveData<Event<Unit>> = _toaster

    val mimeTypes = arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
        "text/plain",
        "application/pdf",
        "application/zip"
    )

    fun getDocumentsRef(): StorageReference {
        return storageReference.child(DOCUMENTS)
    }

    private fun addDocumentData(documentData: DocumentData) {
        documentsCollection.document(documentData.id).set(documentData)
    }

    fun deleteLocal(fileName: String) {
        val localFile = File(storageDirectory, fileName)
        localFile.delete()
    }

    fun deleteOnline(documentData: DocumentData) {
        documentsCollection.document(documentData.id).delete()
        getDocumentsRef().child(documentData.fileName).delete()
    }

    fun addSnapshotListener(): ListenerRegistration {
        return documentsCollection.addSnapshotListener { querySnapshot, error ->
            _documentList.value = querySnapshot?.toObjects(DocumentData::class.java)
            if (error != null) {
                Timber.e("An error occurred ${error.message}")
            }
            checkDataStatus()
        }
    }

    private fun getFileName(uri: Uri): String {
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

    fun moveToLocalAndSaveToFirestore(uri: Uri) {
        val fileName = getFileName(uri)
        _status.value = DataStatus.LOADING
        val inputStream1 = contentResolver.openInputStream(uri)
        try {
            /**
             * save the document data to firestore
             */
            val docRef = getDocumentsRef()
                .child(fileName)
            inputStream1?.let {
                docRef.putStream(it).addOnSuccessListener {
                    docRef.downloadUrl.addOnSuccessListener { url ->
                        Timber.i("url is $url")
                        val documentData =
                            DocumentData(url = url.toString(), fileName = fileName)
                        addDocumentData(documentData)
                    }
                    /**
                     * Save the document locally only after it has successfully been saved to
                     * firestore.
                     * Again, we need 2 input streams because inputStream1 sort of gets exhausted
                     * after use
                     */
                    val inputStream2 = contentResolver.openInputStream(uri)
                    val file = File(storageDirectory, fileName)
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(inputStream2?.readBytes())
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    checkDataStatus()
                }
            }
        } catch (ioE: IOException) {
            _toaster.value = Event(Unit)
            checkDataStatus()
        }
    }

    private fun checkDataStatus() {
        try {
            val docsList = _documentList.value
            if (docsList.isNullOrEmpty()) {
                throw NullPointerException()
            }
            _status.value = DataStatus.NOT_EMPTY
        } catch (npe: NullPointerException) {
            _status.value = DataStatus.EMPTY
        }
    }

    fun getRoot(): String {
        return storageDirectory.toString()
    }
}