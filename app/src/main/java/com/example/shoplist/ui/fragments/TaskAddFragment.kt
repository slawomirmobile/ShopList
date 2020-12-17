package com.example.shoplist.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoplist.R
import com.example.shoplist.databinding.FragmentAddItemsBinding
import com.example.shoplist.viewmodels.TaskAddViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_items.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class TaskAddFragment: Fragment(R.layout.fragment_add_items) {

    private val viewModel: TaskAddViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddItemsBinding.bind(view)

        binding.apply {
            editTextAdd.setText(viewModel.taskName)
            checkBoxImportantAddTask.isChecked = viewModel.taskImportance
        }

        editTextAdd.addTextChangedListener {
            viewModel.taskName = it.toString()
        }

        checkBox_important_add_task.setOnCheckedChangeListener { _, isChecked ->
            viewModel.taskImportance = isChecked
        }




        btn_save.setOnClickListener {
            viewModel.addTask()
        }



        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addTaskEvent.collect {event->
                when(event) {
                    is TaskAddViewModel.AddTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is TaskAddViewModel.AddTaskEvent.NavigateBackWithResult -> {
                        Toast.makeText(requireContext(), "Your task has been added", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }



    }
}