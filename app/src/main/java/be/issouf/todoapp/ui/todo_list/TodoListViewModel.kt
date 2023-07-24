package be.issouf.todoapp.ui.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.issouf.todoapp.data.Todo
import be.issouf.todoapp.data.TodoRepository
import be.issouf.todoapp.util.Routes
import be.issouf.todoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {

    val todos = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deleteTodo: Todo? = null

    fun onEvent(event: TodoListEvent){
        when(event){
            is TodoListEvent.OnTodoClick ->{
                sendUiEvent(UiEvent.Navigation(Routes.ADD_EDIT_TODO + "?todoId=${event.todo.id}"))

            }
            is TodoListEvent.OnAddTodoClick ->{
                sendUiEvent(UiEvent.Navigation(Routes.ADD_EDIT_TODO))
            }
            is TodoListEvent.OnUndoDeleteClick ->{
                deleteTodo?.let { todo ->
                    viewModelScope.launch {
                        repository.insertTodo(todo)
                    }
                }

            }
            is TodoListEvent.OnDeleteTodoClick ->{
                viewModelScope.launch {
                    deleteTodo = event.todo
                    repository.deleteTodo(event.todo)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Todo suprimer",
                        action = "Annuler"
                    ))
                }

                }
            is TodoListEvent.OnDoneChange ->{
                viewModelScope.launch {
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }

            }
        }
    }

    private fun sendUiEvent(event: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}