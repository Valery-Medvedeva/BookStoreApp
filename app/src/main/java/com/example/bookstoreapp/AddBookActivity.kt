package com.example.bookstoreapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.bookstoreapp.data.Book
import com.example.bookstoreapp.databinding.ActivityAddBookBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class AddBookActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddBookBinding.inflate(layoutInflater)
    }
    private val fs by lazy { Firebase.firestore }
    private val storage by lazy { Firebase.storage.reference.child(Const.IMAGES) }

    private val viewModel by lazy {
        ViewModelProvider(this)[AddBookViewModel::class.java]
    }

    private var name = ""
    private var description = ""
    private var price = ""
    private var category = ""
    private var imageUrl = ""
    private lateinit var imageContract: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { it ->
            if (it != null) {
                binding.imageFromGallery.setImageURI(it)
                val task = storage.putBytes(bitmapToByteArray(this@AddBookActivity, it))
                task.addOnSuccessListener { upload ->
                    upload.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                    }
                }
            }
        }

        observeLiveData()
        setListeners()
    }

    private fun setListeners() {
        binding.pickImageBtn.setOnClickListener {
            imageContract.launch("image/*")
        }
        binding.saveBookBtn.setOnClickListener {
            if (inputFieldsIsNotEmpty()) {
                viewModel.saveBook(
                    fs, name, description, price, category, imageUrl)
            } else {
                Toast.makeText(this, "Fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bitmapToByteArray(context: Context, uri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos)
        return baos.toByteArray()
    }

    private fun observeLiveData() {
        viewModel.isSuccess.observe(this) {
            Toast.makeText(this, "The book has been added", Toast.LENGTH_LONG).show()
            val intent = BookListActivity.newIntent(this)
            startActivity(intent)
            finish()
        }
        viewModel.isError.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun inputFieldsIsNotEmpty(): Boolean {
        name = binding.etName.text.toString().trim()
        description = binding.etDescription.text.toString().trim()
        price = binding.etPrice.text.toString().trim()
        category = binding.etCategory.text.toString().trim()
        return (name.isNotEmpty()
                && description.isNotEmpty()
                && price.isNotEmpty()
                && category.isNotEmpty()
                && imageUrl.isNotEmpty()
                )
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AddBookActivity::class.java)
        }
    }
}