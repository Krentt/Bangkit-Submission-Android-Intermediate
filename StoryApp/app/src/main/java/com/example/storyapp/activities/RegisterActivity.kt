package com.example.storyapp.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.api.RegisterRequest
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.view.RegisterViewModel
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registViewModel by viewModels<RegisterViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(false)
        supportActionBar?.hide()

        registViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registViewModel.registResp.observe(this) {
            it.getContentIfNotHandled()?.let { resp ->
                Snackbar.make(window.decorView.rootView, resp, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.edRegisterButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.name_input)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.email_input)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.password_input)
                }
                else -> {
                    val registerRequest = RegisterRequest(name, email, password)
                    registViewModel.register(registerRequest)
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(it.windowToken, 0)
                }
            }
        }
        playAnimation()
    }

    private fun playAnimation() {

        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            startDelay = 500
        }.start()

        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val nama = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.edRegisterButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, nama, email, password, btnRegister)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}