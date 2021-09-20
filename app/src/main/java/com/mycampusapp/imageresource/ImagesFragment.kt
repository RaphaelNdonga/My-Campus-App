package com.mycampusapp.imageresource

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.data.DataStatus
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.ImagesFragmentBinding
import com.mycampusapp.documentresource.DocumentItemListener
import com.mycampusapp.documentresource.DocumentsAdapter
import com.mycampusapp.util.FILE_SIZE_LIMIT
import com.mycampusapp.util.IS_ADMIN
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    companion object {
        fun newInstance() = ImagesFragment()
    }

    private val viewModel by viewModels<ImagesViewModel>()
    private lateinit var binding: ImagesFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var root: String

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = requireContext().getExternalFilesDir(null).toString()

        binding = ImagesFragmentBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            if (binding.galleryFab.visibility == View.GONE) {
                binding.galleryFab.visibility = View.VISIBLE
                binding.cameraFab.visibility = View.VISIBLE
            } else {
                binding.galleryFab.visibility = View.GONE
                binding.cameraFab.visibility = View.GONE
            }
        }
        binding.imagesRefresher.setOnRefreshListener {
            refreshImages()
            binding.imagesRefresher.isRefreshing = false
        }
        val adapter = ImagesAdapter(DocumentsAdapter.DocumentClickListener { imageDoc ->
            val file = File(root, imageDoc.fileName)
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireActivity().applicationContext.packageName + ".provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "image/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(intent)
            } else {
                showDialogBox(imageDoc)
            }
        }, DocumentItemListener { documentData: DocumentData, view: View ->
            val popupMenu = PopupMenu(requireContext(), view)
            val menuInflater = popupMenu.menuInflater
            menuInflater.inflate(R.menu.delete_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
                val isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
                when (menuItem.itemId) {
                    R.id.delete -> {
                        val file = File(root, documentData.fileName)
                        if (file.exists()) {
                            deleteConfirmation(documentData)
                        } else if (isAdmin) {
                            onlineDeleteConfirmation(documentData)
                        }
                        true
                    }
                    else -> true
                }
            })
            popupMenu.show()
        })

        binding.imagesGridView.adapter = adapter

        val getGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val imageUri = result.data?.data
                imageUri?.let { uri ->
                    val fileSize = viewModel.getFileSize(uri) ?: 0L
                    if (fileSize < FILE_SIZE_LIMIT) {
                        val fileName = viewModel.getFileName(uri)
                        viewModel.moveToLocalAndSaveToFirestore(root, fileName, uri)
                    } else {
                        Snackbar.make(requireView(), "The file is too large", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        binding.galleryFab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getGalleryImage.launch(intent)
        }

        binding.cameraFab.setOnClickListener {
            findNavController().navigate(R.id.action_imagesFragment_to_cameraFragment)
        }
        viewModel.images.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            Timber.i("$it")
        })
        viewModel.status.observe(viewLifecycleOwner, { status ->
            status?.let {
                when (it) {
                    DataStatus.EMPTY -> {
                        binding.noImagesPlaceholder.visibility = View.VISIBLE
                        binding.noImagesTxt.visibility = View.VISIBLE
                        binding.noImagesPlaceholder.setImageResource(R.drawable.ic_picture)
                        binding.noImagesTxt.text =
                            requireContext().getString(R.string.no_images_msg)
                    }
                    DataStatus.NOT_EMPTY -> {
                        binding.imagesGridView.visibility = View.VISIBLE
                        binding.noImagesPlaceholder.visibility = View.GONE
                        binding.noImagesTxt.visibility = View.GONE
                    }
                    DataStatus.LOADING -> {
                        binding.imagesGridView.visibility = View.GONE
                        binding.noImagesPlaceholder.visibility = View.VISIBLE
                        binding.noImagesTxt.visibility = View.VISIBLE
                        binding.noImagesTxt.text = requireContext().getString(R.string.loading)
                        binding.noImagesPlaceholder.setImageResource(R.drawable.loading_animation)
                    }
                }
            }
        })
        return binding.root
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

    override fun onStart() {
        super.onStart()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onStop() {
        super.onStop()
        snapshotListener.remove()
    }

    private fun showDialogBox(imageDoc: DocumentData) {
        val builder = AlertDialog.Builder(requireContext(), R.style.MyCampusApp_Dialog)
            .setTitle("Download")
            .setMessage("Do you want to download ${imageDoc.fileName}? ")
            .setNegativeButton(R.string.dialog_negative) { _, _ -> }
            .setPositiveButton(R.string.dialog_positive) { _, _ ->
                viewModel.startLoading()
                val documentRef = viewModel.getImagesRef().child(imageDoc.fileName)
                val documentFile = File(root, imageDoc.fileName)
                documentRef.getFile(documentFile).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Document has been saved successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    refreshImages()
                    viewModel.stopLoading()
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error occurred ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    refreshImages()
                    viewModel.stopLoading()
                }
            }
        builder.create().show()
    }

    private fun refreshImages() {
        snapshotListener.remove()
        snapshotListener = viewModel.addSnapshotListener()
    }
}