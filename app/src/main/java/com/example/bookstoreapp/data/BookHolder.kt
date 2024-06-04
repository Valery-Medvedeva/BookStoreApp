package com.example.bookstoreapp.data

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstoreapp.R

class BookHolder(view: View):RecyclerView.ViewHolder(view) {
    val bookName=view.findViewById<TextView>(R.id.tv_bookName)
    val bookCover=view.findViewById<ImageView>(R.id.iv_bookCover)
}