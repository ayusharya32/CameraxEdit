package com.easycodingg.cameraxedit

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.easycodingg.cameraxedit.databinding.ImageMenuBottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import java.io.File

class ImageMenuBottomSheetFragment(
    private val imageUri: Uri
): BottomSheetDialogFragment() {

    private var _binding: ImageMenuBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.image_menu_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ImageMenuBottomSheetLayoutBinding.bind(view)

        binding.btnShare.setOnClickListener {
            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                Utilities.FILE_PROVIDER_AUTHORITY,
                File(imageUri.path!!)
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.also {
                startActivity(Intent.createChooser(it, "Share image using.."))
            }
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            File(imageUri.path!!).delete()
            Toast.makeText(requireContext(), "Image Deleted Successfully", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onDismissedListener?.let {
            it()
        }
    }
    private var onDismissedListener: (() -> Unit)? = null

    fun setOnDismissedListener(listener: (() -> Unit)) {
        onDismissedListener = listener
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}