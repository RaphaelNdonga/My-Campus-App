package com.mycampusapp.resources

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.mycampusapp.databinding.ResourcesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ResourcesFragment : Fragment() {

    companion object {
        fun newInstance() = ResourcesFragment()
    }

    private val viewModel by viewModels<ResourcesViewModel>()
    private lateinit var binding: ResourcesFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ResourcesFragmentBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            if (binding.addPhoto.visibility == View.GONE) {
                binding.addPhoto.visibility = View.VISIBLE
                binding.addDocument.visibility = View.VISIBLE
            } else {
                binding.addPhoto.visibility = View.GONE
                binding.addDocument.visibility = View.GONE
            }
        }
        val getGalleryImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val imageUri = result.data?.data
                imageUri?.let {
                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmSS",Locale.UK).format(Date())
                    val imagesRef = viewModel.getImagesRef().child("JPEG_$timeStamp.jpg")
                    val task = imagesRef.putFile(it)
                    task.addOnProgressListener { uploadTask->
                        Timber.i(uploadTask.bytesTransferred.toString())
                    }
                }
            }
        binding.addPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getGalleryImage.launch(intent)
        }
        return binding.root
    }
}