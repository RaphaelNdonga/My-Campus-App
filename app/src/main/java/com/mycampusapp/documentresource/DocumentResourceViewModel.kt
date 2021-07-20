package com.mycampusapp.documentresource

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.StorageReference
import com.mycampusapp.data.DocumentData
import com.mycampusapp.util.DOCUMENTS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocumentResourceViewModel @Inject constructor(
    private val storageReference: StorageReference,
    private val courseDocument:DocumentReference
) : ViewModel() {

    fun getDocumentsRef():StorageReference{
        return storageReference.child(DOCUMENTS)
    }
    fun addDocumentData(documentData: DocumentData){
        courseDocument.collection(DOCUMENTS).document(documentData.id).set(documentData)
    }
}