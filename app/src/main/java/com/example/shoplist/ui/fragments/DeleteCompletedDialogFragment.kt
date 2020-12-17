package com.example.shoplist.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.shoplist.viewmodels.TaskDeleteCompleteDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCompletedDialogFragment: DialogFragment() {
    private val viewModel: TaskDeleteCompleteDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog  =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Do you really want to delete all completed task?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") {_,_->
                viewModel.onConfirmClick()
    }.create()
}