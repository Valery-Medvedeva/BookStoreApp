package com.example.bookstoreapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun verification(auth: FirebaseAuth) {
        if (auth.currentUser != null) {
            _user.value = auth.currentUser
        }
    }

    fun createUser(auth: FirebaseAuth, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            _user.value = it.user
        }.addOnFailureListener {
            _error.value = it.message.toString()
        }
    }

    fun signIn(auth: FirebaseAuth, email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            _user.value = it.user
        }.addOnFailureListener {
            _error.value = it.message.toString()
        }
    }

}