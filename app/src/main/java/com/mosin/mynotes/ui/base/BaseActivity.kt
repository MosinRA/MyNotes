package com.mosin.mynotes.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.mosin.mynotes.R
import com.mosin.mynotes.model.NoAuthException

private const val RC_SIGN_IN = 458

abstract class BaseActivity<T, VS : BaseViewState<T>> : AppCompatActivity() {

    abstract val viewModel: BaseViewModel<T, VS>
    abstract val layoutRes: Int
    abstract val ui: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        viewModel.getViewState().observe(this) { t ->
            t?.apply {
                data?.let { data -> renderData(data) }
                error?.let { error -> renderError(error) }
            }
        }
    }

    abstract fun renderData(data: T)

    protected open fun renderError(error: Throwable) {
        when (error) {
            is NoAuthException -> startLoginActivity()
            else -> error.message?.let { showError(it) }
        }
    }

    protected fun showError(error: String) {
        Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.ok_bth_title) { dismiss() }
            show()
        }
    }

    private fun startLoginActivity() {
        val providers = listOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.logo_note)
                        .setTheme(R.style.LoginStyle)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }

}
