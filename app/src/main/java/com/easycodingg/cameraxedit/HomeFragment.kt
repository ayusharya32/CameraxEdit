package com.easycodingg.cameraxedit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.easycodingg.cameraxedit.Utilities.SAVED_IMAGES_DIR
import com.easycodingg.cameraxedit.adapter.SavedImagesListAdapter
import com.easycodingg.cameraxedit.databinding.FragmentHomeBinding
import java.io.File


class HomeFragment: Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedImagesAdapter: SavedImagesListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()

        savedImagesAdapter.submitList(getSavedImagesUriList())

        binding.fab.setOnClickListener {
            if(hasCameraPermission()){
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 0)
            } else {
                findNavController().navigate(R.id.action_homeFragment_to_cameraFragment)
            }
        }
    }

    private fun setupRecyclerView() {
        savedImagesAdapter = SavedImagesListAdapter()

        binding.rvImageList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = savedImagesAdapter
        }

        savedImagesAdapter.setOnItemClickListener { imageUri ->

            val action = HomeFragmentDirections.actionHomeFragmentToViewImageFragment(imageUri, false)
            findNavController().navigate(action)

        }

        savedImagesAdapter.setOnItemLongClickListener {
            val imageMenuBottomSheet = ImageMenuBottomSheetFragment(it)
            imageMenuBottomSheet.show(childFragmentManager, imageMenuBottomSheet.tag)

            imageMenuBottomSheet.setOnDismissedListener {
                savedImagesAdapter.submitList(getSavedImagesUriList())
            }
        }
    }

    private fun getSavedImagesUriList(): List<Uri> {
        val savedImagesUriList = mutableListOf<Uri>()
        val savedImagesDirPath = requireContext().getExternalFilesDir(SAVED_IMAGES_DIR)?.path + "/"
        val savedImagesDir = File(savedImagesDirPath)

        savedImagesDir.listFiles()?.let { savedImagesFileList ->
            if(savedImagesFileList.isNotEmpty()) {

                for(imageFile in savedImagesFileList) {
                    savedImagesUriList.add(Uri.fromFile(imageFile))
                }
            }
        }
        return savedImagesUriList.toList()
    }

    private fun hasCameraPermission() =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
