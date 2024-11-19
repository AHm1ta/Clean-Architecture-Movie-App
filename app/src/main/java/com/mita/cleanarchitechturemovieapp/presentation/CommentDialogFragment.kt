package com.mita.cleanarchitechturemovieapp.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.databinding.FragmentCommentDialogBinding

class CommentDialogFragment(private val onDismissCallback: () -> Unit) : BottomSheetDialogFragment(
    R.layout.fragment_comment_dialog
) {

    private lateinit var binding: FragmentCommentDialogBinding

    /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         return AlertDialog.Builder(requireContext())
             .setTitle("Dialog Title")
             .setMessage("This is an example dialog.")
             .setPositiveButton("Close") { _, _ ->
                 dismiss()
             }
             .create()
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            requireDialog().requestWindowFeature(Window.FEATURE_NO_TITLE)
            requireDialog().window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding = FragmentCommentDialogBinding.bind(view)
        binding.closeIV.setOnClickListener {
            dismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback() // Notify parent activity/fragment to restore PlayerView
    }
}