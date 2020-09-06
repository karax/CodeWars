package com.jeankarax.codewars

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeankarax.codewars.model.api.APICalls
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.UserResponse
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.Executor

@RunWith(MockitoJUnitRunner::class)
class ServiceTest {

    private val compositeDisposable = CompositeDisposable()
    private val userName: String = "leeeroy"

    @get: Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiCalls: APICalls

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
    }

    @Before
    fun setupRxSchedulers() {
        val immediate = object: Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run() }, true)
            }
        }

        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> immediate }
    }

    @After
    fun clearDisposable(){
        compositeDisposable.clear()
    }

    @Test
    fun whenGetUser_andIsResponseSuccess_thenReturnUserSuccessful(){
        val userAPI = UserAPI(apiCalls)
        val mockedUser: Single<UserResponse> = getMockedSuccessUser()
        var testUserName = ""
        var isLoading: Boolean = true

        Mockito.`when`(apiCalls.getUser(userName)).thenReturn(mockedUser)

        fun getUserResponse(){
            userAPI.getUser(userName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<UserResponse>() {
                override fun onSuccess(response: UserResponse) {
                    isLoading = false
                    testUserName = response.username
                }

                override fun onError(e: Throwable) {

                }

            })
        }
        getUserResponse()
        Assert.assertFalse(isLoading)
        Assert.assertEquals(testUserName, "leeroy")

    }

    @Test
    fun whenGetUser_andIsResponseError_thenReturnUserSuccessful(){
        val userAPI = UserAPI(apiCalls)
        val mockedUser: Single<UserResponse> = getMockedError()
        var isLoading: Boolean = true
        var isError: Boolean = false

        Mockito.`when`(apiCalls.getUser(userName)).thenReturn(mockedUser)

        fun getUserResponse(){
            userAPI.getUser(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<UserResponse>() {
                    override fun onSuccess(response: UserResponse) {
                        isLoading = false
                    }
                    override fun onError(e: Throwable) {
                        isLoading = false
                        isError = true
                    }

                })
        }
        getUserResponse()
        Assert.assertFalse(isLoading)
        Assert.assertTrue(isError)

    }

    private fun getMockedSuccessUser(): Single<UserResponse> {
        return Single.just(UserResponse(username = "leeroy", name = "Leroy Jenkins"))
    }

    private fun getMockedError(): Single<UserResponse>{
        return Single.error(Throwable())
    }

}