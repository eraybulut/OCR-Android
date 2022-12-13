package com.application.ocr

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.application.ocr.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.permissionx.guolindev.PermissionX


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var imageBitmap: Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.scannedImage.setOnClickListener {
            PermissionX.init(this)
                .permissions(android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        ImagePicker.with(this)
                            .compress(1024)
                            .crop()
                            .start()
                            binding.loadingLottie.visibility = View.VISIBLE
                    }
                }
        }

        binding.copy.setOnClickListener {
            if (!binding.resultTextView.text.isBlank()){
                Toast.makeText(this,getString(R.string.copy),Toast.LENGTH_SHORT).show()
                binding.resultTextView.visibility = View.VISIBLE
                val text= binding.resultTextView.text.toString()
                copy(text)
            }
        }

        binding.share.setOnClickListener {
            if (!binding.resultTextView.text.isBlank()){
                binding.resultTextView.visibility = View.VISIBLE
                val text=binding.resultTextView.text.toString()
                shareText(text)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            imageBitmap = bitmap
            binding.scannedImage.setImageBitmap(bitmap)
            processImage()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }


    private fun processImage() {
        if (imageBitmap != null) {
            val image = imageBitmap?.let {
                InputImage.fromBitmap(it, 0)
            }
            image?.let {
                recognizer.process(it)
                    .addOnSuccessListener {
                        binding.resultTextView.text= it.text
                        binding.resultTextView.visibility = View.VISIBLE
                        binding.loadingLottie.visibility = View.GONE
                    }
            }
        }
    }

    private fun copy(text: String){
        val myClipboard = this@MainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip = ClipData.newPlainText("label", text)
        myClipboard.setPrimaryClip(myClip)
    }

    private fun shareText(text: String){
        val textIntent=Intent()
        textIntent.setAction(Intent.ACTION_SEND)
        textIntent.putExtra(Intent.EXTRA_TEXT,text)
        textIntent.setType("text/plain")
        startActivity(textIntent)

    }
}