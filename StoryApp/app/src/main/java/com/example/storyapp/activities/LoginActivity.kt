package com.example.storyapp.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.api.LoginRequest
import com.example.storyapp.customView.EmailEditText
import com.example.storyapp.customView.PasswordEditText
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.view.LoginViewModel
import com.example.storyapp.view.MainViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    private val mainViewModel by viewModels<MainViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    private lateinit var emailText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);
        setContentView(binding.root)
        showLoading(false)
        supportActionBar?.hide()

        binding.edRegister.setOnClickListener(this)
        binding.edLoginButton.setOnClickListener(this)

        emailText = binding.edLoginEmail
        button = binding.edLoginButton
        passwordEditText = binding.edLoginPassword

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.isSuccess.observe(this) {
            authCheck(it)
        }

        loginViewModel.loginResp.observe(this) {
            it.getContentIfNotHandled()?.let { resp ->
                Snackbar.make(window.decorView.rootView, resp, Snackbar.LENGTH_SHORT).show()
            }
        }

        emailText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        playAnimation()
    }

    private fun setMyButtonEnable(){
        val result = emailText.text
        val password = passwordEditText.text
        button.isEnabled = android.util.Patterns.EMAIL_ADDRESS.matcher(result.toString()).matches() && result.toString().isNotEmpty() &&
                password.toString().length >= 8
    }

    private fun playAnimation() {

        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            startDelay = 500
        }.start()

        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin =
            ObjectAnimator.ofFloat(binding.edLoginButton, View.ALPHA, 1f).setDuration(500)
        val line = ObjectAnimator.ofFloat(binding.line, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvAkun, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.edRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(desc, btnRegister)
        }


        AnimatorSet().apply {
            playSequentially(title, email, password, btnLogin, line, together)
            start()
        }

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ed_register -> {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            R.id.ed_login_button -> {
                val email = binding.edLoginEmail.text.toString()
                val password = binding.edLoginPassword.text.toString()
                when {
                    email.isEmpty() -> {
                        binding.edLoginEmail.error = getString(R.string.email_input)
                    }
                    password.isEmpty() -> {
                        binding.edLoginPassword.error = getString(R.string.password_input)
                    }
                    else -> {
                        val loginRequest = LoginRequest(email, password)
                        loginViewModel.login(loginRequest)
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(p0.windowToken, 0)
                    }
                }


            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun authCheck(isSuccess: Boolean) {
        if (isSuccess) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            mainViewModel.isToastShown = false
            intent.putExtra("show_toast", getString(R.string.logged_in))
            startActivity(intent)
            finish()
        }
    }


}