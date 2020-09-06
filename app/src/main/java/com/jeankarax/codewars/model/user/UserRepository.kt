package com.jeankarax.codewars.model.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserRepository
@Inject
constructor(
    private val userAPI: UserAPI
): IUserRepository
{

    private val disposable = CompositeDisposable()
    val user = MediatorLiveData<UserResponse>()
    val error = MediatorLiveData<Throwable>()

    override fun getUserFromAPI(userName: String): Single<UserResponse> {
        return userAPI.getUser(userName)
    }

    override fun getUser(userName: String) {
        disposable.add(userAPI.getUser(userName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<UserResponse>(){
                override fun onSuccess(t: UserResponse) {
                    user.postValue(t)
                }
                override fun onError(e: Throwable) {
                    error.postValue(e)
                }

            })
        )
    }

    override fun getUserObservable(): LiveData<UserResponse> {
        return user
    }

    override fun getErrorObservable(): LiveData<Throwable> {
        return error
    }


}