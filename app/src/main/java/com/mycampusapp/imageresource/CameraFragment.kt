package com.mycampusapp.imageresource

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.storage.StorageReference
import com.mycampusapp.R
import com.mycampusapp.data.DocumentData
import com.mycampusapp.databinding.FragmentCameraBinding
import com.mycampusapp.util.CAMERA_PERMISSIONS
import com.mycampusapp.util.IMAGES
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null

    private val viewModel by viewModels<CameraViewModel>()

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionMap ->
            if (
                permissionMap.entries.all {
                    it.value == true
                }
            ) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions denied. Cannot access camera",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(CAMERA_PERMISSIONS)
        }

        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
        binding.imageCapture.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                captureAnimation()
            }
            takePhoto()
        }
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun captureAnimation() {
        binding.root.postDelayed({
                binding.root.foreground = ColorDrawable(Color.WHITE)
                binding.root.postDelayed({
                    binding.root.foreground = null
                }, 50L)
            },100L,)
    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder().build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        startLoading()
        val root = requireActivity().getExternalFilesDir(null)
        val timeStamp = System.currentTimeMillis()
        val fileName = "JPEG_$timeStamp.jpg"
        val file = File(root, fileName)
        val imagesRef = viewModel.getImagesRef().child(fileName)
        imageCapture?.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    imagesRef.putFile(file.toUri()).addOnSuccessListener {
                        imagesRef.downloadUrl.addOnSuccessListener {
                            viewModel.addFirestoreData(
                                DocumentData(url = it.toString(), fileName = fileName)
                            )
                        }
                        Toast.makeText(
                            requireContext(),
                            "Image saved successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        Navigation.findNavController(requireView()).navigateUp()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireContext(),
                        "An error occurred while taking the image",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
    }

    private fun allPermissionsGranted(): Boolean {
        return CAMERA_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startLoading() {
        binding.switchCamera.visibility = View.GONE
        binding.imageCapture.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.loading_animation)
        binding.imageCapture.isClickable = false
    }
}