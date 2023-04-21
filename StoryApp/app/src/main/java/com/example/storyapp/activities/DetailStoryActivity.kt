package com.example.storyapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.api.Story
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.view.DetailStoryViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val detailViewModel by viewModels<DetailStoryViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val idStory = intent.getStringExtra("id_story")

        if (idStory != null) {
            detailViewModel.getDetail(idStory)
        }

        detailViewModel.isLoading.observe(this){
            showLoading(it)
        }

        detailViewModel.detailResp.observe(this){
            it.getContentIfNotHandled()?.let { resp ->
                Snackbar.make(window.decorView.rootView, resp, Snackbar.LENGTH_SHORT).show()
            }
        }

        detailViewModel.storyDetail.observe(this){
            setDetailStory(it)
        }

    }

    private fun setDetailStory(story: Story?){
        Glide.with(this)
            .load(story?.photoUrl)
            .into(binding.ivDetailPhoto)

        binding.tvDetailName.text = story?.name
        binding.tvDetailDate.text = convertDate(story?.createdAt.toString())
        binding.tvDetailDescription.text = story?.description

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun convertDate(date: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        val parseDate = date.let { inputFormat.parse(it) }

        return parseDate?.let { outputFormat.format(it) }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}