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
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.R
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.ImageResourceFragmentBinding
import com.mycampusapp.documentresource.DocumentsAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ImageResourceFragment : Fragment() {

    companion object {
        fun newInstance() = ImageResourceFragment()
    }

    private val viewModel by viewModels<ImageResourceViewModel>()
    private lateinit var binding: ImageResourceFragmentBinding
    private lateinit var snapshotListener: ListenerRegistration
    private lateinit var root: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = requireContext().getExternalFilesDir(null).toString()

        binding = ImageResourceFragmentBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            if (binding.galleryFab.visibility == View.GONE) {
                binding.galleryFab.visibility = View.VISIBLE
                binding.cameraFab.visibility = View.VISIBLE
            } else {
                binding.galleryFab.visibility = View.GONE
                binding.cameraFab.visibility = View.GONE
            }
        }
        val adapter = ImageResourceAdapter(DocumentsAdapter.DocumentClickListener { imageDoc ->
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
        val getCameraImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.UK).format(Date())
                val fileName = "JPEG_$timeStamp.jpg"

                /**
                 * The image data can be directly obtained from the bitmap by compressing it into
                 * the output stream instead of using an input stream to obtain the image data.
                 */
                val cameraBitmap = result.data?.extras?.get("data") as Bitmap
                val outputStream = ByteArrayOutputStream()
                cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val inputStream = ByteArrayInputStream(outputStream.toByteArray())


                val imagesRef = viewModel.getImagesRef().child(fileName)

                val task = imagesRef.putStream(inputStream)
                task.addOnProgressListener { uploadTask ->
                    Timber.i(uploadTask.bytesTransferred.toString())
                }.addOnSuccessListener {
                    imagesRef.downloadUrl.addOnSuccessListener { imageUri ->
                        viewModel.addFirestoreData(
                            DocumentData(url = imageUri.toString(), fileName = fileName)
                        )
                        /**
                         * Only save the file locally after it has been sent to the online
                         * database
                         */
                        try {
                            /**
                             * Using a second input stream because it seams the first input stream
                             * gets exhausted
                             */
                            val reopenedInputStream =
                                ByteArrayInputStream(outputStream.toByteArray())
                            val imageFile = File(root, fileName)
                            viewModel.writeDataToFile(reopenedInputStream, imageFile)

                        } catch (ioE: IOException) {
                            Toast.makeText(
                                requireContext(),
                                "An error occurred while saving the file",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "An error occurred while downloading the url",
                            Toast.LENGTH_LONG
                        ).show()
                        Timber.i("Exception is $it")
                    }
                }
            }
        binding.cameraFab.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getCameraImage.launch(intent)
        }
        viewModel.images.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            Timber.i("$it")
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
}