package com.mosin.mynotes.viewModel

import com.mosin.mynotes.model.auth.NoAuthException
import com.mosin.mynotes.model.repository.Repository
import com.mosin.mynotes.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(private val repository: Repository)
    : BaseViewModel<Boolean>() {

    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}
