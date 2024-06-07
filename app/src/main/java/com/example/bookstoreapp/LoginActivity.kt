package com.example.bookstoreapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.bookstoreapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val auth by lazy { Firebase.auth }
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java]}
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.verification(auth)
        binding.buttonCreateUser.setOnClickListener {
           if(inputFieldsIsNotEmpty()) {
              viewModel.createUser(auth, email, password)
           } else{
               Toast.makeText(this, "fill in all the fields", Toast.LENGTH_LONG).show()
           }
        }
        binding.buttonSignIn.setOnClickListener {
            if(inputFieldsIsNotEmpty()) {
                viewModel.signIn(auth, email, password)
            } else{
                Toast.makeText(this, "fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
        viewModel.error.observe(this){
            if (it!=null){
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.user.observe(this){
            if (it!=null){
                val intent=BookListActivity.newIntent(this)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun inputFieldsIsNotEmpty():Boolean {
        email = binding.editTextTextEmailAddress.text.toString().trim()
        password = binding.editTextNumberPassword.text.toString().trim()
        return (email.isNotEmpty() && password.isNotEmpty())
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}