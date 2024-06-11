package com.example.bookstoreapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstoreapp.data.Book
import com.example.bookstoreapp.data.RvAdapter
import com.example.bookstoreapp.databinding.ActivityBookListBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
        setListeners()
        setupSwipeListener()
    }

    private fun setupSwipeListener() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                TODO("Not yet implemented")
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = rvAdapter.currentList[viewHolder.adapterPosition]
                fs.collection(Const.BOOKS).document(item.name)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@BookListActivity,
                            "The book successfully deleted!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@BookListActivity,
                            it.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rv)
    }

    private fun setListeners() {
        binding.addButton.setOnClickListener {
            addBookToCollection()
        }
        binding.signOutBtn.setOnClickListener {
            auth.signOut()
            val intent = LoginActivity.newIntent(this)
            startActivity(intent)
        }

        listener = fs.collection(Const.BOOKS).addSnapshotListener { value, error ->
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