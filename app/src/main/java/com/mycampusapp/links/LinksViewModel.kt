package com.mycampusapp.links

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.data.Links

class LinksViewModel(private val linksCollection: CollectionReference) : ViewModel() {
    private val _links = MutableLiveData<List<Links>>()
    val links:LiveData<List<Links>> = _links

    fun addSnapshotListener(): ListenerRegistration {
        return linksCollection.addSnapshotListener { querySnapshot, error ->
            _links.value = querySnapshot?.toObjects(Links::class.java)
        }
    }
}