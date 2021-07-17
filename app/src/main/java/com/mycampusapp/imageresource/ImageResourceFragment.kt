package com.mycampusapp.imageresource

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
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.ListenerRegistration
import com.mycampusapp.databinding.ImageResourceFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ImageResourceFragment : Fragment() {

    companion object {
        fun newInstance() = ImageResourceFragment()
    }

    private val viewModel by viewModels<ImageResourceViewModel>()
    private lateinit var binding: ImageResourceFragmentBinding
    private lateinit var snapshotListener:ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val adapter = ImageResourceAdapter()
        binding.imagesGridView.adapter = adapter
        val getGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val imageUri = result.data?.data
                imageUri?.let {
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.UK).format(Date())
                    val imagesRef = viewModel.getImagesRef().child("JPEG_$timeStamp.jpg")
                    val task = imagesRef.putFile(it)
                    task.addOnProgressListener { uploadTask ->
                        Timber.i(uploadTask.bytesTransferred.toString())
                    }.addOnSuccessListener {
                        imagesRef.downloadUrl.addOnSuccessListener { imageUri ->
                            viewModel.addFirestoreData(imageUri.toString())
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
        binding.galleryFab.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getGalleryImage.launch(intent)
        }
        val getCameraImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val cameraBitmap = result.data?.extras?.get("data") as Bitmap
                val outputStream = ByteArrayOutputStream()
                cameraBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val inputStream = ByteArrayInputStream(outputStream.toByteArray())
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.UK).format(Date())
                val imagesRef = viewModel.getImagesRef().child("JPEG_$timeStamp.jpg")
                val task = imagesRef.putStream(inputStream)
                task.addOnProgressListener { uploadTask ->
                    Timber.i(uploadTask.bytesTransferred.toString())
                }.addOnSuccessListener {
                    imagesRef.downloadUrl.addOnSuccessListener { imageUri ->
                        viewModel.addFirestoreData(imageUri.toString())
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
        viewModel.images.observe(viewLifecycleOwner,{
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
}