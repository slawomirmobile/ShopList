package com.example.shoplist.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.shoplist.di.ApplicationScope
import com.example.shoplist.model.TaskDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TaskDeleteCompleteDialogViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTask()
    }
}