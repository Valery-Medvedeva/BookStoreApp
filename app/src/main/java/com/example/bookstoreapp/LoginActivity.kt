package com.example.bookstoreapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookstoreapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val auth by lazy { Firebase.auth }
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonCreateUser.setOnClickListener {
           if(inputFieldsIsNotEmpty()) {
               createUser(auth, email, password)
           } else{
               Toast.makeText(this, "fill in all the fields", Toast.LENGTH_LONG).show()
           }
        }
        binding.buttonSignIn.setOnClickListener {
            if(inputFieldsIsNotEmpty()) {
                signIn(auth, email, password)
            } else{
                Toast.makeText(this, "fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun inputFieldsIsNotEmpty():Boolean {
        email = binding.editTextTextEmailAddress.text.toString().trim()
        password = binding.editTextNumberPassword.text.toString().trim()
        return (email.isNotEmpty() && password.isNotEmpty())
    }

    private fun createUser(auth: FirebaseAuth, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = BookListActivity.newIntent(this)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn(auth: FirebaseAuth, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = BookListActivity.newIntent(this)
                startActivity(intent)
            } else {
                Toast.makeText(this,  it.exception?.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}