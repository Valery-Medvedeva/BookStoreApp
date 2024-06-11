package com.example.bookstoreapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
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

    override fun onResume() {
        super.onResume()
        val categories = resources.getStringArray(R.array.categories)
        val arrayAdapter = ArrayAdapter(this, R.layout.drop_item, categories)
        binding.autoCompleteTv.setAdapter(arrayAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        registerImageContract()
        observeLiveData()
        setListeners()
    }

    private fun setListeners() {
        binding.autoCompleteTv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                category = position.toString()
                Log.d("PERA", "выбрали жанр книги")
            }
        binding.pickImageBtn.setOnClickListener {
            imageContract.launch("image/*")
            Log.d("PERA", " запустили уствновку картинки из галереи на обложку")
        }
        binding.saveBookBtn.setOnClickListener {
            if (inputFieldsIsNotEmpty()) {
                viewModel.saveBook(
                    fs, name, description, price, category, imageUrl
                )
                Log.d("PERA", "сохранили книгу")
            } else {
                Toast.makeText(this, "Fill in all the fields", Toast.LENGTH_LONG).show()
                Log.d("PERA", "поля пустые")
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

    private fun registerImageContract(){
        imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { it ->
            if (it != null) {
                val task=storage
                    .child(it.lastPathSegment.toString())
                    .putBytes(bitmapToByteArray(this@AddBookActivity, it))
                task.addOnSuccessListener { upload ->
                    upload.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        Log.d("PERA", "сохранили картинку в хранилище")
                    }
                }.addOnFailureListener {
                    Log.d("PERA", "The image could not be saved to the storage")
                }
                binding.imageFromGallery.setImageURI(it)
                Log.d("PERA", "установили картинку из галереи на обложку")
            }
        }
    }

    private fun observeLiveData() {
        viewModel.isSuccess.observe(this) {
            Toast.makeText(this, "The book has been added", Toast.LENGTH_LONG).show()
            Log.d("PERA", "книга добавлена- выведен тост")
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