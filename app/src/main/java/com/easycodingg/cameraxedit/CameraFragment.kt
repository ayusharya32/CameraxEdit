package com.easycodingg.cameraxedit

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.easycodingg.cameraxedit.Utilities.FILE_NAME_FORMAT
import com.easycodingg.cameraxedit.databinding.FragmentCameraBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment: Fragment(R.layout.fragment_camera) {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageCapture: ImageCapture
    private lateinit var camera: Camera
    private lateinit var preview: Preview
    private var isFlashOn = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var progressDialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCameraBinding.bind(view)

        setupProgressDialog()

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindPreview()
        }, ContextCompat.getMainExecutor(requireContext()))

        binding.ivClick.setOnClickListener {
            clickAndSaveImage()
        }

        binding.ivFlash.setOnClickListener {
            if(isFlashOn){
                isFlashOn = false
                binding.ivFlash.setImageResource(R.drawable.ic_flash_off)
                imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
            } else {
                isFlashOn = true
                binding.ivFlash.setImageResource(R.drawable.ic_flash_on)
                imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
            }
        }

        binding.ivFlip.setOnClickListener {
            if(lensFacing == CameraSelector.LENS_FACING_BACK){
                lensFacing = CameraSelector.LENS_FACING_FRONT
                binding.ivFlash.visibility = View.INVISIBLE
            } else {
                lensFacing = CameraSelector.LENS_FACING_BACK
                binding.ivFlash.visibility = View.VISIBLE
            }
            bindPreview()
        }


    }

    private fun clickAndSaveImage() {
        progressDialog.show()
        val timeStamp = SimpleDateFormat(FILE_NAME_FORMAT, Locale.UK).format(System.currentTimeMillis())

        val filePath = requireContext().getExternalFilesDir("temp")?.path + "/IMG$timeStamp.jpg"
        val file = File(filePath)

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    progressDialog.dismiss()

                    val imageUri = outputFileResults.savedUri ?: Uri.fromFile(file)
                    val action = CameraFragmentDirections.actionCameraFragmentToViewImageFragment(imageUri)
                    findNavController().navigate(action)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Some error occurred", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                }
            })
    }

    private fun setupProgressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog_layout, binding.root, false)

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogOverlay).apply {
            setView(dialogView)
            setCancelable(false)
        }.create()
    }

    private fun bindPreview() {
        preview = Preview.Builder()
                .setTargetResolution(Size(480,640))
                .build()

        val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

        preview.setSurfaceProvider(binding.pvCamera.surfaceProvider)

        imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(Size(480,640))
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()

        try {
            val cameraProvider = cameraProvider
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, imageCapture,
                    preview)
        } catch(e: Exception) {
            Log.d("Camaro", "Use Case Binding Failed")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}