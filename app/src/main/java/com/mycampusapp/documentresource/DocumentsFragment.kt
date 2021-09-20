package com.mycampusapp.documentresource

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.data.DataStatus
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.DocumentsFragmentBinding
import com.mycampusapp.util.EventObserver
import com.mycampusapp.util.IS_ADMIN
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class DocumentsFragment : Fragment() {

    private val viewModel: DocumentsViewModel by viewModels()
    private lateinit var binding: DocumentsFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DocumentsFragmentBinding.inflate(inflater, container, false)
        val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)

        val getFileResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val fileUri = result.data?.data

                fileUri?.let {
                    viewModel.moveToLocalAndSaveToFirestore(it)
                }
            }

        binding.addDocument.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, viewModel.mimeTypes)
            }
            getFileResult.launch(intent)
        }
        binding.documentsRefresher.setOnRefreshListener {
            refreshDocuments()
            binding.documentsRefresher.isRefreshing = false
        }

        val adapter = DocumentsAdapter(DocumentsAdapter.DocumentClickListener { documentData ->
            val file = File(viewModel.getRoot(), documentData.fileName)
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
                downloadConfirmation(documentData)
            }
        }, DocumentItemListener { documentData: DocumentData, view: View ->
            val popupMenu = PopupMenu(requireContext(), view)
            val menuInflater = popupMenu.menuInflater

            menuInflater.inflate(R.menu.delete_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener {
                val file = File(viewModel.getRoot(), documentData.fileName)
                when (it.itemId) {
                    R.id.delete -> {
                        if(file.exists()){
                            deleteConfirmation(documentData)
                        }else if(isAdmin){
                            onlineDeleteConfirmation(documentData)
                        }
                        true
                    }
                    else -> true
                }
            }
            popupMenu.show()
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
        viewModel.status.observe(viewLifecycleOwner, { status ->
            status?.let {
                when (it) {
                    DataStatus.EMPTY -> {
                        binding.noDocsImage.visibility = View.VISIBLE
                        binding.noDocsTxt.visibility = View.VISIBLE
                        binding.noDocsImage.setImageResource(R.drawable.ic_document)
                        binding.noDocsTxt.text = requireContext().getString(R.string.no_docs_msg)
                    }
                    DataStatus.NOT_EMPTY -> {
                        binding.documentRecyclerView.visibility = View.VISIBLE
                        binding.noDocsImage.visibility = View.GONE
                        binding.noDocsTxt.visibility = View.GONE
                    }
                    DataStatus.LOADING -> {
                        binding.documentRecyclerView.visibility = View.GONE
                        binding.noDocsImage.visibility = View.VISIBLE
                        binding.noDocsTxt.visibility = View.VISIBLE
                        binding.noDocsImage.setImageResource(R.drawable.loading_animation)
                        binding.noDocsTxt.text = requireContext().getString(R.string.loading)
                    }
                }
            }
        })

        return binding.root
    }

    private fun deleteConfirmation(documentData: DocumentData) {
        val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
        builder.setTitle(getString(R.string.dialog_delete))
        builder.setMessage("Delete local file?")

        builder.setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
                viewModel.deleteLocal(
                    documentData.fileName
                )
            if (isAdmin) {
                onlineDeleteConfirmation(documentData)
            }
        }
        builder.setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }

        builder.create().show()
    }

    private fun onlineDeleteConfirmation(documentData: DocumentData) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
        builder.setTitle(getString(R.string.dialog_delete))
        builder.setMessage("Delete online file?")

        builder.setPositiveButton(getString(R.string.dialog_positive)) { _, _ ->
            viewModel.deleteOnline(documentData)
        }
        builder.setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }

        builder.create().show()
    }

    private fun downloadConfirmation(documentData: DocumentData) {
        val builder = AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
            .setTitle("Download")
            .setMessage("Do you want to download ${documentData.fileName}? ")
            .setNegativeButton(R.string.dialog_negative) { _, _ -> }
            .setPositiveButton(R.string.dialog_positive) { _, _ ->
                val documentRef = viewModel.getDocumentsRef().child(documentData.fileName)
                val documentFile = File(viewModel.getRoot(), documentData.fileName)
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

    private fun refreshDocuments() {
        snapshotListener.remove()
        snapshotListener = viewModel.addSnapshotListener()
    }
}