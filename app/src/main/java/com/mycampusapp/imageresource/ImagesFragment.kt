package com.mycampusapp.imageresource

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.data.DataStatus
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.ImagesFragmentBinding
import com.mycampusapp.documentresource.DocumentsAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    companion object {
        fun newInstance() = ImagesFragment()
    }

    private val viewModel by viewModels<ImagesViewModel>()
    private lateinit var binding: ImagesFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var root: String

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
        })

        binding.imagesGridView.adapter = adapter

        val getGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val imageUri = result.data?.data
                imageUri?.let { uri ->
                    val fileName = viewModel.getFileName(uri)
                    val fis = requireContext().contentResolver.openInputStream(uri)
                    fis?.let { inputStream ->
                        val imagesRef = viewModel.getImagesRef().child(fileName)
                        val task = imagesRef.putStream(inputStream)
                        task.addOnProgressListener { uploadTask ->
                            Timber.i(uploadTask.bytesTransferred.toString())
                        }.addOnSuccessListener {
                            imagesRef.downloadUrl.addOnSuccessListener { imageUri ->
                                viewModel.addFirestoreData(
                                    DocumentData(
                                        url = imageUri.toString(),
                                        fileName = viewModel.getFileName(uri)
                                    )
                                )
                                /**
                                 * Only save the file locally after it has been sent to the online
                                 * database
                                 */
                                try {
                                    val imageFile = File(root, fileName)
                                    val reopenedInputStream =
                                        requireContext().contentResolver.openInputStream(uri)
                                    viewModel.writeDataToFile(reopenedInputStream, imageFile)
                                } catch (ioE: IOException) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Unable to create file",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(
                                    requireContext(),
                                    "An error occurred while downloading the url",
                                    Toast.LENGTH_LONG
                                ).show()
                                Timber.i("Exception is $exception")
                            }
                        }
                    }
                }
            }
        binding.galleryFab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getGalleryImage.launch(intent)
        }

        val imageCapture = ImageCapture.Builder()

        binding.cameraFab.setOnClickListener {

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
                        binding.noImagesTxt.text = requireContext().getString(R.string.no_images_msg)
                    }
                    DataStatus.NOT_EMPTY -> {
                        binding.noImagesPlaceholder.visibility = View.GONE
                        binding.noImagesTxt.visibility = View.GONE
                    }
                    DataStatus.LOADING -> {
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
                val documentRef = viewModel.getImagesRef().child(imageDoc.fileName)
                val documentFile = File(root, imageDoc.fileName)
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

    private fun refreshImages() {
        snapshotListener.remove()
        snapshotListener = viewModel.addSnapshotListener()
    }
}