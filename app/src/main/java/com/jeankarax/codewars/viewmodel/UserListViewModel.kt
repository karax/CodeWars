package com.jeankarax.codewars.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jeankarax.codewars.model.di.DaggerUserComponent
import com.jeankarax.codewars.model.response.UserResponse
import com.jeankarax.codewars.model.user.IUserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserListViewModel(application: Application) : AndroidViewModel(application) {

    val userLiveData by lazy { MutableLiveData<UserResponse>() }
    val loadError by lazy { MutableLiveData<Boolean>() }
    val loading by lazy { MutableLiveData<Boolean>() }

    private val disposable = CompositeDisposable()


    @Inject
    lateinit var userRepository: IUserRepository

    init{
        DaggerUserComponent.create().inject(this)
    }

    fun getUser(userName: String) {
        loading.value = true
        disposable.add(
            userRepository.getUserFromAPI(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<UserResponse>() {
                    override fun onSuccess(user: UserResponse) {
                        loading.value = false
                        userLiveData.value = user
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loading.value = false
                        loadError.value = true
                    }

                })
        )
    }

}
