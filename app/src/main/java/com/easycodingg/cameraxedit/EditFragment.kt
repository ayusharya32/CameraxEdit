package com.easycodingg.cameraxedit

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.easycodingg.cameraxedit.adapter.FilterListAdapter
import com.easycodingg.cameraxedit.databinding.FragmentEditBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class EditFragment: Fragment(R.layout.fragment_edit) {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val args: EditFragmentArgs by navArgs()

    private lateinit var filterAdapter: FilterListAdapter
    private var filterPreviewUriList: MutableList<FilterItem> = mutableListOf()
    private lateinit var progressDialog: AlertDialog
    private lateinit var originalImageUri: Uri
    private lateinit var currentFilterUri: Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEditBinding.bind(view)
        setHasOptionsMenu(true)

        originalImageUri = args.imageUri
        currentFilterUri = originalImageUri

        setupRecyclerView()
        setupProgressDialog()

        progressDialog.show()
        binding.ivEditImage.setImageURI(originalImageUri)
        setupPreviewImages()

    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun setupPreviewImages() = viewLifecycleOwner.lifecycleScope.launch {
        val imageBitmap = getImageBitmapFromUri(originalImageUri)
                .copy(Bitmap.Config.ARGB_8888, false)

        val filterList = Utilities.filterList
        val filterNameList = Utilities.filterNameList

        withContext(Dispatchers.IO) {
            val gpuImage = GPUImage(requireContext())

            for(i in filterList.indices) {
                gpuImage.setFilter(filterList[i])

                val previewFilePath = requireContext().getExternalFilesDir("temp/edited")?.path + "/${i + 1}.jpg"
                val previewFile = File(previewFilePath)

                try {
                    val outputStream = FileOutputStream(previewFile)
                    gpuImage.getBitmapWithFilterApplied(imageBitmap)
                            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    filterPreviewUriList.add(FilterItem(filterNameList[i], Uri.fromFile(previewFile)))

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error Loading Filters", Toast.LENGTH_SHORT).show()
                    }
                    break
                }
            }
        }

        imageBitmap.recycle()
        filterAdapter.list = filterPreviewUriList
        filterAdapter.notifyDataSetChanged()
        progressDialog.dismiss()
    }

    private fun getImageBitmapFromUri(imageUri: Uri): Bitmap {
        return if(Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        } else {
            val file = File(imageUri.path!!)
            val source = ImageDecoder.createSource(file)
            ImageDecoder.decodeBitmap(source)

        }
    }

    private fun setupRecyclerView() {

        filterAdapter = FilterListAdapter(listOf())
        filterAdapter.setOnItemClickListener {
            currentFilterUri = it

            Glide.with(requireContext())
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivEditImage)
        }

        binding.rvFilters.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = filterAdapter
        }

    }

    private fun setupProgressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog_layout, binding.root, false)

        progressDialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogOverlay).apply {
            setView(dialogView)
            setCancelable(false)
        }.create()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.miDone -> {
                applyChangesToOriginalImage()
                val action = EditFragmentDirections.actionEditFragmentToViewImageFragment(originalImageUri)
                findNavController().navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyChangesToOriginalImage() {
        if(currentFilterUri != originalImageUri) {
            val originalImageFile = File(originalImageUri.path!!)
            val editedImageFile = File(currentFilterUri.path!!)

            editedImageFile.copyTo(originalImageFile, true)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}