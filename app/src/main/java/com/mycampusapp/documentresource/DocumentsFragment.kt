package com.mycampusapp.documentresource

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.DocumentsFragmentBinding
import com.mycampusapp.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File


@AndroidEntryPoint
class DocumentsFragment : Fragment() {

    private val viewModel: DocumentsViewModel by viewModels()
    private lateinit var binding: DocumentsFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var root: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DocumentsFragmentBinding.inflate(inflater, container, false)
        root = requireContext().getExternalFilesDir(null).toString()

        Timber.i("The root directory is $root")

        val getFileResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val fileUri = result.data?.data

                fileUri?.let {
                    val fileName = viewModel.getFileName(it)
                    viewModel.moveToLocalAndSaveToFirestore(root, fileName)
                }
            }

        binding.addDocument.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, viewModel.mimeTypes)
            }
            getFileResult.launch(intent)
        }

        val adapter = DocumentsAdapter(DocumentsAdapter.DocumentClickListener { documentData ->
            val file = File(root, documentData.fileName)
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "*/*")
                    putExtra(Intent.EXTRA_MIME_TYPES, viewModel.mimeTypes)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(intent)

            } else {
                showDialogBox(documentData)
            }
        })
        binding.documentRecyclerView.adapter = adapter
        viewModel.documentList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
        viewModel.toaster.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(
                requireContext(),
                "There was an error saving your file.",
                Toast.LENGTH_LONG
            ).show()
        })

        return binding.root
    }

    private fun showDialogBox(documentData: DocumentData) {
        val builder = AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
            .setTitle("Download")
            .setMessage("Do you want to download ${documentData.fileName}? ")
            .setNegativeButton(R.string.dialog_negative) { _, _ -> }
            .setPositiveButton(R.string.dialog_positive) { _, _ ->
                val documentRef = viewModel.getDocumentsRef().child(documentData.fileName)
                val documentFile = File(root, documentData.fileName)
                documentRef.getFile(documentFile).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Document has been saved successfully",
                        Toast.LENGTH_LONG
                    ).show()
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        builder.create().show()
    }

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }
}