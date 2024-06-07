package com.example.bookstoreapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookstoreapp.data.Book
import com.google.firebase.firestore.FirebaseFirestore
class AddBookViewModel:ViewModel() {

    private val _isSuccess=MutableLiveData<Unit>()
    val isSuccess: LiveData<Unit>
        get()=_isSuccess

    private val _isError=MutableLiveData<String>()
    val isError: LiveData<String>
        get()=_isError

    fun saveBook(fs: FirebaseFirestore,
                 name:String, description:String, price:String, category:String, imageUrl:String){
        fs.collection(Const.BOOK).document().set(
            Book(name, description, price, category, imageUrl)
        )
            .addOnSuccessListener { _isSuccess.value=Unit }
            .addOnFailureListener { _isError.value=it.message.toString()}
    }
}