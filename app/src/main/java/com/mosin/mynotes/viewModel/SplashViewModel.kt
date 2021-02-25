package com.mosin.mynotes.viewModel

import com.mosin.mynotes.model.auth.NoAuthException
import com.mosin.mynotes.model.repository.Repository
import com.mosin.mynotes.ui.base.BaseViewModel
import com.mosin.mynotes.ui.splash.SplashViewState

class SplashViewModel(private val repository: Repository)
    : BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever { user ->
            viewStateLiveData.value = user?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}