package com.example.shoplist.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoplist.model.Task
import com.example.shoplist.model.TaskDao
import com.example.shoplist.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskEditViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
): ViewModel (){

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
    set(value) {
        field = value
        state.set("taskName", value)
    }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    private val editTaskChannel = Channel<EditTaskChannel>()
    val editTaskEvent = editTaskChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidMessage("Name cannot empty")
            return
        }
        if (task != null) {
            val updateTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updateTask)
        }
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        editTaskChannel.send(EditTaskChannel.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidMessage(text: String) = viewModelScope.launch {
        editTaskChannel.send(EditTaskChannel.ShowInvalidInputMessage(text))
    }


    sealed class EditTaskChannel {
        data class ShowInvalidInputMessage(val msg: String): EditTaskChannel()
        data class NavigateBackWithResult(val result: Int): EditTaskChannel()
    }

}