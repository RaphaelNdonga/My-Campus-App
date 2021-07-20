package com.mycampusapp.documentresource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.DocumentData
import com.mycampusapp.util.DOCUMENTS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocumentResourceViewModel @Inject constructor(
    private val storageReference: StorageReference,
    courseDocument: DocumentReference
) : ViewModel() {
    private val _documentList = MutableLiveData<List<DocumentData>>()
    val documentList: LiveData<List<DocumentData>> = _documentList
    private val documentsCollection = courseDocument.collection(DOCUMENTS)

    fun getDocumentsRef(): StorageReference {
        return storageReference.child(DOCUMENTS)
    }

    fun addDocumentData(documentData: DocumentData) {
        documentsCollection.document(documentData.id).set(documentData)
    }

    fun addSnapshotListener(): ListenerRegistration {
        return documentsCollection.addSnapshotListener { querySnapshot, error ->
            _documentList.value = querySnapshot?.toObjects(DocumentData::class.java)
        }
    }
}