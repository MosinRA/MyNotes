package com.mosin.mynotes.ui.splash

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.mosin.mynotes.R
import com.mosin.mynotes.databinding.ActivitySplashBinding
import com.mosin.mynotes.ui.base.BaseActivity
import com.mosin.mynotes.ui.main.MainActivity
import com.mosin.mynotes.viewModel.SplashViewModel

private const val START_DELAY = 1000L

class SplashActivity : BaseActivity<Boolean?, SplashViewState>() {

    override val viewModel: SplashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }
    override val ui: ActivitySplashBinding
            by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override val layoutRes: Int = R.layout.activity_splash

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper())
                .postDelayed({
                    viewModel.requestUser()
                }, START_DELAY)
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
    }
}
