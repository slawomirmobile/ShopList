package com.example.shoplist.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoplist.R
import com.example.shoplist.databinding.FragmentEditItemsBinding
import com.example.shoplist.viewmodels.TaskEditViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_items.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class EditTaskFragment: Fragment(R.layout.fragment_edit_items) {

    private val viewModel: TaskEditViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentEditItemsBinding.bind(view)

        binding.apply {
            editTextAdd.setText(viewModel.taskName)
            checkBox_important_add_task.isChecked = viewModel.taskImportance
            textViewCreatedTask.text = "Created: ${viewModel.task?.createDateFormat}"
        }

        editTextAdd.addTextChangedListener {
            viewModel.taskName = it.toString()
        }

        checkBox_important_add_task.setOnCheckedChangeListener { _, isChecked ->
            viewModel.taskImportance = isChecked
        }

        btn_save.setOnClickListener {
            viewModel.onSaveClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.editTaskEvent.collect { event ->
                when(event) {
                    is TaskEditViewModel.EditTaskChannel.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TaskEditViewModel.EditTaskChannel.NavigateBackWithResult -> {
                        binding.editTextAdd.clearFocus()
                        setFragmentResult("add_edit_request", bundleOf("add_edit_result" to event.result))
                        Toast.makeText(requireContext(), "Updated your items", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }

    }

}