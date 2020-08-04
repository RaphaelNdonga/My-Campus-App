package com.example.android.mycampusapp.assignments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentAssignmentsBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AssignmentsFragment : Fragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore
    private lateinit var fetchText: TextView
    val QUOTE_KEY = "quote"
    val AUTHOR_KEY = "author"

    private lateinit var quoteView: EditText
    private lateinit var authorView: EditText
    private lateinit var quoteText: String
    private lateinit var authorText: String
    private lateinit var docReference: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAssignmentsBinding>(
            inflater,
            R.layout.fragment_assignments,
            container,
            false
        )

        docReference = firestore.document("sampleData/inspiration")
        quoteView = binding.quoteEdit
        authorView = binding.philosopherEdit
        val sendBtn = binding.sendDataBtn
        val fetchBtn = binding.fetchDataBtn
        fetchText = binding.fetchedDataText

        sendBtn.setOnClickListener {
            sendData()
        }
        fetchBtn.setOnClickListener {
            fetchData()
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        docReference.addSnapshotListener(requireActivity()) {
                documentSnapshot: DocumentSnapshot?,firestoreException: FirebaseFirestoreException? ->
            if (documentSnapshot != null) {
                quoteText = documentSnapshot.getString(QUOTE_KEY)!!
                authorText = documentSnapshot.getString(AUTHOR_KEY)!!
                val combinedText = "$quoteText /n ~$authorText"
                fetchText.text = combinedText
            } else {
                Timber.i("Error $firestoreException")
            }
        }
    }

    private fun fetchData() {
        docReference.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                quoteText = documentSnapshot.getString(QUOTE_KEY)!!
                authorText = documentSnapshot.getString(AUTHOR_KEY)!!
                val combinedText = "$quoteText /n ~$authorText"
                fetchText.text = combinedText
            }
        }

    }

    private fun sendData() {
        quoteText = quoteView.text.toString()
        authorText = authorView.text.toString()

        if (quoteText.isEmpty() || authorText.isEmpty()) {
            return
        }
        val dataToSave = HashMap<String, Any>()
        dataToSave[QUOTE_KEY] = quoteText
        dataToSave[AUTHOR_KEY] = authorText

        docReference.set(dataToSave).addOnSuccessListener {
            Timber.i("Document has been saved!")
        }.addOnFailureListener { exception ->
            Timber.i("Document has not been saved due to $exception")
        }
    }
}