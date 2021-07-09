package com.mycampusapp.links

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.data.DataStatus
import com.mycampusapp.data.Links
import timber.log.Timber
import java.lang.NullPointerException

class LinksViewModel(private val linksCollection: CollectionReference) : ViewModel() {
    private val _links = MutableLiveData<List<Links>>()
    val links: LiveData<List<Links>> = _links

    private val _status = MutableLiveData<DataStatus>()
    val status: LiveData<DataStatus> = _status

    fun addSnapshotListener(): ListenerRegistration {
        _status.value = DataStatus.LOADING
        return linksCollection.addSnapshotListener { querySnapshot, error ->
            _links.value = querySnapshot?.toObjects(Links::class.java)
            checkDataStatus()
            if (error != null) {
                Timber.i("An error occurred $error")
            }
        }
    }

    private fun checkDataStatus() {
        try {
            val links = _links.value

            if (links.isNullOrEmpty()) {
                throw NullPointerException("Links are null")
            }
            _status.value = DataStatus.NOT_EMPTY
        }catch (npe:NullPointerException){
            _status.value = DataStatus.EMPTY
        }
    }

    fun deleteList(list: List<Links>) {
        list.forEach {
            linksCollection.document(it.id).delete()
        }
    }
}