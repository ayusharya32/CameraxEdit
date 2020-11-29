package com.easycodingg.cameraxedit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.easycodingg.cameraxedit.Utilities.FILE_NAME_FORMAT
import com.easycodingg.cameraxedit.Utilities.SAVED_IMAGES_DIR
import com.easycodingg.cameraxedit.databinding.FragmentViewImageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ViewImageFragment: Fragment(R.layout.fragment_view_image) {

    private var _binding: FragmentViewImageBinding? = null
    private val binding get() = _binding!!
    private val args: ViewImageFragmentArgs by navArgs()
    private lateinit var imageUri: Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        _binding = FragmentViewImageBinding.bind(view)

        imageUri = args.imageUri

        doInitialSetup()

        binding.btnSaveImage.setOnClickListener {
            saveFinalImage()
            findNavController().navigate(R.id.action_viewImageFragment_to_homeFragment)
        }

    }

    private fun doInitialSetup() {
        binding.btnSaveImage.isVisible = args.isEditable

        Glide.with(requireContext())
            .load(imageUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivViewImage)
    }

    private fun saveFinalImage() {
        val timeStamp = SimpleDateFormat(FILE_NAME_FORMAT, Locale.UK).format(System.currentTimeMillis())

        val filePath = requireContext().getExternalFilesDir(SAVED_IMAGES_DIR)?.path + "/IMG$timeStamp.jpg"
        val finalImageFile = File(filePath)
        val tempImageFile = File(imageUri.path!!)

        tempImageFile.copyTo(finalImageFile)
        tempImageFile.delete()

        Snackbar.make(requireView(), "Image Saved Successfully", Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if(args.isEditable) {
            inflater.inflate(R.menu.view_image_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.miCrop -> setupAndStartCropActivity()
            R.id.miEdit -> {
                val action =
                    ViewImageFragmentDirections.actionViewImageFragmentToEditFragment(imageUri)
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAndStartCropActivity() {
        val options = UCrop.Options().apply {
            setCompressionQuality(100)
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setToolbarColor(ContextCompat.getColor(requireContext(), R.color.teal_900))
            setRootViewBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray))
            setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.dark_gray))
            setToolbarWidgetColor(ContextCompat.getColor(requireContext(), R.color.white))
            setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.teal_900))
            setToolbarTitle("Crop Image")
        }

        UCrop.of(imageUri, imageUri).apply {
            withOptions(options)
            start(requireContext(), this@ViewImageFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                imageUri = UCrop.getOutput(data)!!

                binding.ivViewImage.setImageURI(null)
                binding.ivViewImage.setImageURI(imageUri)
            }
        } else if(resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(requireContext(), "Some error occurred while cropping", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}