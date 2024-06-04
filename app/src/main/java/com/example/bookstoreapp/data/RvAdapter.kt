package com.example.bookstoreapp.data

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.bookstoreapp.R

class RvAdapter:ListAdapter<Book, BookHolder>(DiffItemCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookHolder(view)
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        val book=getItem(position)
        holder.bookName.text=book.name
        Glide.with(holder.itemView).load(book.imageUrl).into(holder.bookCover)
    }
}