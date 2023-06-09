package com.example.storyapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.ListStoriesAdapter
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.api.ListStoryItem
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.preferences.AuthPreference
import com.example.storyapp.view.LoginViewModel
import com.example.storyapp.view.MainViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val loginViewModel by viewModels<LoginViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    private val mainViewModel by viewModels<MainViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.title_stories)

        // Check user login
        val authPreference = AuthPreference(this)
        if (authPreference.getKey() == "") {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val showToast = intent.getStringExtra("show_toast")
        if (showToast != null && !mainViewModel.isToastShown) {
            Toast.makeText(this, showToast, Toast.LENGTH_SHORT).show()
            mainViewModel.isToastShown = true
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }

        mainViewModel.getResp.observe(this) {
            it.getContentIfNotHandled()?.let { resp ->
                Snackbar.make(window.decorView.rootView, resp, Snackbar.LENGTH_SHORT).show()
            }
        }
        getData()
    }

    private fun getData(){
        val adapter = ListStoriesAdapter()
        binding.listStories.layoutManager = LinearLayoutManager(this)
        binding.listStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        mainViewModel.listStories.observe(this){
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra("id_story", data.id)
                startActivity(intent)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                loginViewModel.logout()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                return true
            }
            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.map -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                return true
            }
            else -> {
                return true
            }
        }
    }
}