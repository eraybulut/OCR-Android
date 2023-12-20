package com.application.ocr

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.application.ocr.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var scannedText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initializeListeners()
    }

    private fun initializeListeners() {
        with(binding) {
            icScan.onVibrationClick {
                if (getReadAndWritePermission()) {
                    contentLauncher.launch("image/*")
                }
            }

            btnCopy.onVibrationClick {
                if (scannedText.isNullOrEmpty()) {
                    showToast("Please scan an image")
                    return@onVibrationClick
                }
                showToast("Copied to clipboard")
                copyToClipboard(scannedText!!)
            }

            btnSend.onVibrationClick {
                if (scannedText.isNullOrEmpty()) {
                    showToast("Please scan an image")
                    return@onVibrationClick
                }
                showToast("Sending...")
                shareText(scannedText!!)
            }
        }
    }

    private val contentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                with(binding) {
                    icScan.setImageURI(uri)
                    val photoBitmap = uri.toBitmap(this@MainActivity) ?: return@with
                    processImage(photoBitmap)
                }
            }
        }


    private fun processImage(photoBitmap: Bitmap) {
        val image = photoBitmap.let {
            InputImage.fromBitmap(it, 0)
        }
        image.let {
            recognizer.process(it).addOnSuccessListener { scannedText ->
                this.scannedText = scannedText.text
                binding.resultTextView.text = scannedText.text
            }.addOnFailureListener { exception ->
                Log.e("KEY", exception.toString())
            }
        }

    }
}