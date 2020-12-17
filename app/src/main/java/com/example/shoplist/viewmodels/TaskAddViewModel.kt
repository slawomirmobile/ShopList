package com.example.shoplist.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoplist.model.Task
import com.example.shoplist.model.TaskDao
import com.example.shoplist.ui.ADD_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskAddViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
): ViewModel() {

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

    private val addTaskEventChannel = Channel<AddTaskEvent>()
    val addTaskEvent = addTaskEventChannel.receiveAsFlow()




   private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addTaskEventChannel.send(AddTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    fun addTask() {
        val newTask = Task(name = taskName, important = taskImportance)
        createTask(newTask)


    }


  private fun showInvalidMessage(text: String) = viewModelScope.launch {
      addTaskEventChannel.send(AddTaskEvent.ShowInvalidInputMessage(text))
  }




    sealed class AddTaskEvent {
        data class ShowInvalidInputMessage(val msg: String): AddTaskEvent()
        data class NavigateBackWithResult(val result: Int): AddTaskEvent()
    }

}


