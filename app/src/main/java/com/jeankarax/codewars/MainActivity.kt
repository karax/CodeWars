package com.jeankarax.codewars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.jeankarax.codewars.viewmodel.UserListViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var viewModel = UserListViewModel(this.application)
        setContentView(R.layout.activity_main)
        var teste: String = ""
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
            .create(UserListViewModel::class.java)
        viewModel.getUser("g964")
    }
}