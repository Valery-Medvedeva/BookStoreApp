package com.example.bookstoreapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bookstoreapp.data.Book
import com.example.bookstoreapp.data.RvAdapter
import com.example.bookstoreapp.databinding.ActivityBookListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class BookListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityBookListBinding.inflate(layoutInflater) }
    private lateinit var rvAdapter: RvAdapter

    private val auth by lazy { Firebase.auth }
    private val fs by lazy { Firebase.firestore }

    private var bookList = mutableListOf<Book>()

    private lateinit var listener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRv()

        binding.addButton.setOnClickListener {
            addBookToCollection()
        }
        binding.signOutBtn.setOnClickListener {
            auth.signOut()
            val intent = LoginActivity.newIntent(this)
            startActivity(intent)
        }

        listener = fs.collection(Const.BOOK).addSnapshotListener { value, error ->
            bookList = value?.toObjects(Book::class.java)!!
            rvAdapter.submitList(bookList.toList())
        }
    }



    private fun addBookToCollection() {
        val intent = AddBookActivity.newIntent(this)
        startActivity(intent)
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