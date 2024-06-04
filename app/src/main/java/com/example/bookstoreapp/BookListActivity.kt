package com.example.bookstoreapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bookstoreapp.data.Book
import com.example.bookstoreapp.data.RvAdapter
import com.example.bookstoreapp.databinding.ActivityBookListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class BookListActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityBookListBinding.inflate(layoutInflater)
    }

    private lateinit var rvAdapter: RvAdapter

    private var bookList = mutableListOf<Book>()

    private lateinit var listener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRv()
        val fs = Firebase.firestore
        val storage = Firebase.storage.reference.child("Book cover images")

        binding.addButton.setOnClickListener {
            val task = storage.child("The black cloud.PNG").putBytes(
                bitmapToByteArray(this@BookListActivity)
            )
            task.addOnSuccessListener { snapshot ->
                snapshot.metadata?.reference?.downloadUrl?.addOnCompleteListener {
                    addBookToCollection(fs, it.result)
                }
            }
        }

        listener = fs.collection("books").addSnapshotListener { value, error ->
            bookList = value?.toObjects(Book::class.java)!!
            Log.d("BOOKLIST", bookList.toString())
            rvAdapter.submitList(bookList.toList())
        }
    }

    private fun bitmapToByteArray(context: Context): ByteArray {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.theblackcloud)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos)
        return baos.toByteArray()
    }

    private fun addBookToCollection(fs: FirebaseFirestore, url: Uri) {
        fs.collection("books").document("My favourite books").set(
            Book(
                "The black cloud",
                "space",
                "240.0",
                "non-fiction",
                imageUrl = url.toString()
            )
        )
    }

    private fun setupRv() {
        val rv = binding.rv
        rvAdapter = RvAdapter()
        rv.adapter = rvAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        listener.remove()
    }

    companion object {

        fun newIntent(context: Context) = Intent(context, BookListActivity::class.java)
    }
}