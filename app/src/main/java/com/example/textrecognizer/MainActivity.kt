package com.example.textrecognizer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.textrecognizer.databinding.ActivityMainBinding
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.button.setOnClickListener {
            pickImageFromGallery()
        }
    }

    companion object{
        private var IMAGE_PICK_CODE = 1
    }

    private fun pickImageFromGallery() {

        val intent = Intent().apply {
            action = Intent.ACTION_PICK
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            binding.imageView.setImageURI(data?.data)
            if (data != null) {
                startTextRecognizing(data.data)
            }
        }
    }

    fun startTextRecognizing(uri:Uri) {
        if (binding.imageView.drawable != null) {
            //Initialize input object
            val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)

            //Initialize the on-device detector
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    // Task completed successfully
                    binding.textView.text = processTextBlock(firebaseVisionText)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    binding.textView.text = "Fail!"
                }
        } else {
            Toast.makeText(this, "Fail!", Toast.LENGTH_LONG).show()
        }
    }

    fun startCloudBasedTextRecognizing(uri:Uri) {
        if (binding.imageView.drawable != null) {

            //Initialize input object
            val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)

            //Initialize the cloud-based detector
            val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionDocumentText ->
                    // Task completed successfully
                    binding.textView.text = processTextBlockForCloudBasedModel(firebaseVisionDocumentText)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    binding.textView.text = "Fail!"
                }
        } else {
            Toast.makeText(this, "Fail!", Toast.LENGTH_LONG).show()
        }
    }

    //Option 1 for creating FirebaseVisionImage
    private fun imageFromBitmap(bitmap: Bitmap) : FirebaseVisionImage {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        return image
    }

    //Option 2 for creating FirebaseVisionImage
    private fun imageFromPath(context: Context, uri: Uri): FirebaseVisionImage? {
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(context,uri)
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun processTextBlock(result: FirebaseVisionText): String {
        val resultText = result.text

        for (block in result.textBlocks) {
            val blockText = block.text
            for (line in block.lines) {
                val lineText = line.text
                for (element in line.elements) {
                    val elementText = element.text
                    val elementConfidence = element.confidence
                }
            }
        }
        return resultText
    }

    private fun processTextBlockForCloudBasedModel(result: FirebaseVisionDocumentText): String{
        return result.text
    }
}
