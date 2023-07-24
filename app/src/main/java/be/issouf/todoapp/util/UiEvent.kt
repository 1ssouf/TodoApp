package be.issouf.todoapp.util

sealed class UiEvent{
    object PopBackStack: UiEvent()
    data class Navigation(val route: String): UiEvent()
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ): UiEvent()
}
