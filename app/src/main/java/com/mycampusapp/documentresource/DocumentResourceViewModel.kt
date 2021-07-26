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
import com.mycampusapp.data.DocumentData
import com.mycampusapp.util.DOCUMENTS
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DocumentResourceViewModel @Inject constructor(
    private val storageReference: StorageReference,
    private val contentResolver: ContentResolver,
    courseDocument: DocumentReference
) : ViewModel() {
    private val _documentList = MutableLiveData<List<DocumentData>>()
    val documentList: LiveData<List<DocumentData>> = _documentList
    private val documentsCollection = courseDocument.collection(DOCUMENTS)
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

    fun addSnapshotListener(): ListenerRegistration {
        return documentsCollection.addSnapshotListener { querySnapshot, error ->
            _documentList.value = querySnapshot?.toObjects(DocumentData::class.java)
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

    fun moveToLocalAndSaveToFirestore(root: String, fileName: String) {
        val file = File(root, fileName)
        val fos = FileOutputStream(file)
        val fis = contentResolver.openInputStream(file.toUri())
        try {
            /**
             * This is where we move the file to local app external storage
             */
            fos.write(fis?.readBytes())
            fos.flush()
            fos.close()
            /**
             * After movement is successful, save the document data to firestore
             */
            val docRef = getDocumentsRef()
                .child(fileName)
            docRef.putFile(file.toUri()).addOnSuccessListener {
                docRef.downloadUrl.addOnSuccessListener { url ->
                    Timber.i("url is $url")
                    val documentData =
                        DocumentData(url = url.toString(), fileName = fileName)
                    addDocumentData(documentData)
                }
            }
        } catch (ioE: IOException) {

        }
    }
}