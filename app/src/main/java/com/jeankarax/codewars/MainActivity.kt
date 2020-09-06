package com.jeankarax.codewars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jeankarax.codewars.viewmodel.UserListViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel: UserListViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
            .create(UserListViewModel::class.java)

        viewModel.getUser("g964")

        viewModel.userLiveData.observe(this, Observer { user ->
                run {
                    val teste = user.name
                    print(teste)
                }
            })

        viewModel.loading.observe(this, Observer { isLoading ->
            run {
                //TODO implement loading
            }
        })

        viewModel.errorLiveData.observe(this, Observer { isErrorReturned ->
            run{
                //TODO implement error
            }
        })
    }
}