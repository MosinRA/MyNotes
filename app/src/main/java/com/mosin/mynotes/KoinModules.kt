package com.mosin.mynotes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mosin.mynotes.model.repository.Repository
import com.mosin.mynotes.model.provider.FireStoreProvider
import com.mosin.mynotes.model.provider.IRemoteDataProvider
import com.mosin.mynotes.viewModel.MainViewModel
import com.mosin.mynotes.viewModel.NoteViewModel
import com.mosin.mynotes.viewModel.SplashViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FireStoreProvider(get(), get()) } bind IRemoteDataProvider::class
    single { Repository(get()) }
}

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}

val mainModule = module {
    viewModel { MainViewModel(get()) }
}

val noteModule = module {
    viewModel { NoteViewModel(get()) }
}