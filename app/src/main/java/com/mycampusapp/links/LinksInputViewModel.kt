package com.mycampusapp.links

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.mycampusapp.data.Links
import timber.log.Timber

class LinksInputViewModel(private val collection: CollectionReference) : ViewModel() {

    fun addData(link:Links){
        collection.document(link.id).set(link).addOnSuccessListener {
            Timber.i("The data was added successfully")
        }.addOnFailureListener {
            Timber.i("Error occurred with exception $it")
        }
    }
}