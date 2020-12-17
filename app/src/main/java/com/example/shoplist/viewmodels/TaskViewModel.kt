package com.example.shoplist.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.shoplist.model.PreferenceManager
import com.example.shoplist.model.SortOrder
import com.example.shoplist.model.Task
import com.example.shoplist.model.TaskDao
import com.example.shoplist.ui.ADD_TASK_RESULT_OK
import com.example.shoplist.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager,
    @Assisted private val state: SavedStateHandle
): ViewModel() {


    //Search in editText Items
    val searchQuery = state.getLiveData("searchQuery", "")

    val preferenceFlow = preferenceManager.preferenceFlow

    //Swipe undo delete
    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
         searchQuery.asFlow(),
        preferenceFlow
    ) { query, filtredPreferences->
        Pair(query, filtredPreferences)
    }.flatMapLatest {(query, filtredPreferences) ->
        taskDao.getAllTasks(query, filtredPreferences.sortOrder, filtredPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSaveSortOrder(sortOrder)
    }

    fun onHideCompletedUpdate(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateSaveHideCompleted(hideCompleted)
    }



    fun onTaskSelected(task:Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskSelectedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    //Swiped Delete
    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }


    fun addNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun addOnResult(result: Int) {
        when(result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

//    DELETE COMPLETED TASK
    fun onDeleteAllCompletedTask() = viewModelScope.launch {
    tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedTaskScreen)
}






    sealed class TasksEvent {
        object NavigateToAddTaskScreen: TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task): TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task): TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String): TasksEvent()
        object NavigateToDeleteAllCompletedTaskScreen: TasksEvent()
    }

}

