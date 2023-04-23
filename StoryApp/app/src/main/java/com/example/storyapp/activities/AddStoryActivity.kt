package com.example.storyapp.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.helper.ViewModelFactory
import com.example.storyapp.view.AddStoryViewModel
import com.example.storyapp.view.MainViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val addStoryViewModel by viewModels<AddStoryViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    private val mainViewModel by viewModels<MainViewModel>() {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        showLoading(false)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        addStoryViewModel.previewImageBitmap.observe(this) { bitmap ->
            binding.previewImageView.setImageBitmap(bitmap)
        }

        addStoryViewModel.getFile.observe(this) { file ->
            getFile = file
        }

        binding.cameraXButton.setOnClickListener {
            addStoryViewModel.onCameraClicked()
        }

        binding.galleryButton.setOnClickListener {
            addStoryViewModel.onGalleryButtonClicked()
        }

        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString()
            val getFile = getFile
            when {
                description.isEmpty() -> {
                    binding.edAddDescription.error = getString(R.string.desc_input)
                }
                getFile == null -> {
                    Toast.makeText(
                        this@AddStoryActivity,
                        getString(R.string.img_input),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    addStoryViewModel.uploadStory(description, getFile)
                }
            }
        }

        addStoryViewModel.isSuccess.observe(this) {
            uploadedCheck(it)
        }

        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        addStoryViewModel.takePhotoIntent.observe(this) { uri ->
            launcherIntentCamera.launch(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    uri
                )
            )
        }

        addStoryViewModel.galleryIntent.observe(this) {
            launcherIntentGallery.launch(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            })
        }

        addStoryViewModel.uploadResp.observe(this) {
            it.getContentIfNotHandled()?.let { resp ->
                Snackbar.make(window.decorView.rootView, resp, Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        addStoryViewModel.onTakePhotoResult(result.resultCode)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            addStoryViewModel.onGalleryResult(uri)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun uploadedCheck(isSuccess: Boolean) {
        if (isSuccess) {
            val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mainViewModel.isToastShown = false
            intent.putExtra("show_toast", getString(R.string.uploaded))
            startActivity(intent)
            finish()
        }
    }
}