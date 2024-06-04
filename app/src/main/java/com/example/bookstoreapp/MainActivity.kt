package com.example.bookstoreapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookstoreapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.button.setOnClickListener {
            val intent=BookListActivity.newIntent(this)
            startActivity(intent)
        }
    }
}