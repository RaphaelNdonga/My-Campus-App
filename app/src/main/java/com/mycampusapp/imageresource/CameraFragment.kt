package com.mycampusapp.imageresource

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mycampusapp.databinding.FragmentCameraBinding
import com.mycampusapp.util.CAMERA_CODE_PERMISSIONS
import com.mycampusapp.util.CAMERA_PERMISSIONS
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val executor = Executors.newSingleThreadExecutor()

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
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(CAMERA_PERMISSIONS)
        }

        binding.switchCamera.setOnClickListener {
            cameraSelector = if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                CameraSelector.DEFAULT_FRONT_CAMERA
            }else{
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }
        return binding.root
    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        }
        val imageCapture = ImageCapture.Builder().build()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted(): Boolean {
        return CAMERA_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}