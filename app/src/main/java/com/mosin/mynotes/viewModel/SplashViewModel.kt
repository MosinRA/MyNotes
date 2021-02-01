package com.mosin.mynotes.viewModel

import com.mosin.mynotes.model.NoAuthException
import com.mosin.mynotes.model.Repository
import com.mosin.mynotes.ui.base.BaseViewModel
import com.mosin.mynotes.ui.splash.SplashViewState

class SplashViewModel(private val repository: Repository = Repository)
    : BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever { user ->
            viewStateLiveData.value = user?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}