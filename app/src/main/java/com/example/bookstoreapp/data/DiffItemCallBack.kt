package com.example.bookstoreapp.data

import androidx.recyclerview.widget.DiffUtil

class DiffItemCallBack:DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem===newItem
    }

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
        return oldItem==newItem
    }
}