package com.jeankarax.codewars

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jeankarax.codewars.model.api.APICalls
import com.jeankarax.codewars.model.api.ChallengeAPI
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.*
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
        val mockedUser: LiveData<BaseApiResponse<UserResponse>> = getMockedSuccessUser()

        Mockito.`when`(apiCalls.getUser(userName)).thenReturn(mockedUser)

        userAPI.getUser(userName)
        Assert.assertEquals(userAPI.userLiveData.getOrAwaitValue().status, Status.SUCCESS)
        Assert.assertEquals(userAPI.userLiveData.getOrAwaitValue().data?.username, userName)
    }

    @Test
    fun whenGetUser_andIsResponseError_thenReturnUserError(){
        val userAPI = UserAPI(apiCalls)
        val mockedUser: LiveData<BaseApiResponse<UserResponse>> = getMockedErrorUser()

        Mockito.`when`(apiCalls.getUser(userName)).thenReturn(mockedUser)

        userAPI.getUser(userName)
        Assert.assertEquals(userAPI.userLiveData.getOrAwaitValue().status, Status.ERROR)
    }

    @Test
    fun whenGetChallenge_andIsResponseSuccess_thenReturnChallengeSuccessful(){
        val challengeAPI = ChallengeAPI(apiCalls)

        Mockito.`when`(apiCalls.getChallenge("12")).thenReturn(getMockedChallenge())

        challengeAPI.getChallenge("12")
        Assert.assertEquals(challengeAPI.challengeLiveData.getOrAwaitValue().status, Status.SUCCESS)
        Assert.assertEquals(challengeAPI.challengeLiveData.getOrAwaitValue().data?.id, "12")
    }

    @Test
    fun whenGetChallenge_andIsResponseError_thenReturnChallengeError(){
        val challengeAPI = ChallengeAPI(apiCalls)

        Mockito.`when`(apiCalls.getChallenge("12")).thenReturn(getMockedChallengeError())

        challengeAPI.getChallenge("12")
        Assert.assertEquals(challengeAPI.challengeLiveData.getOrAwaitValue().status, Status.ERROR)
    }

    @Test
    fun whenGetChallengesLists_andIsResponseSuccess_thenReturnChallengesSuccessful(){
        val challengeAPI = ChallengeAPI(apiCalls)

        Mockito.`when`(apiCalls.getCompletedChallenges(userName, 1)).thenReturn(getMockedCompletedChallenges2())
        Mockito.`when`(apiCalls.getAuthoredChallenges(userName)).thenReturn(getMockedAuthoredChallenges2())

        challengeAPI.getChallengesList(userName, 1)
        Assert.assertEquals(challengeAPI.challengesListLiveData.getOrAwaitValue().status, Status.SUCCESS)
        Assert.assertEquals(challengeAPI.challengesListLiveData.getOrAwaitValue().data?.get(0)?.data?.get(0)?.id, "13")
        Assert.assertEquals(challengeAPI.challengesListLiveData.getOrAwaitValue().data?.get(1)?.data?.get(0)?.id, "11")

    }

    private fun getMockedSuccessUser(): LiveData<BaseApiResponse<UserResponse>>{
        val response = MutableLiveData<BaseApiResponse<UserResponse>>()
        val user = UserResponse(username = "leeeroy")
        response.value = BaseApiResponse.create(Response.success(user))
        return response
    }

    private fun getMockedErrorUser(): LiveData<BaseApiResponse<UserResponse>>{
        val response = MutableLiveData<BaseApiResponse<UserResponse>>()
        response.value = BaseApiResponse.create(Throwable())
        return response
    }

    private fun getMockedChallenge(): LiveData<BaseApiResponse<ChallengeResponse>>{
        val response = MutableLiveData<BaseApiResponse<ChallengeResponse>>()
        val challenge = ChallengeResponse(id = "12", name = "Black Temple")
        response.value = BaseApiResponse.create(Response.success(challenge))
        return response
    }

    private fun getMockedChallengeError(): LiveData<BaseApiResponse<ChallengeResponse>> {
        val response = MutableLiveData<BaseApiResponse<ChallengeResponse>>()
        response.value = BaseApiResponse.create(Throwable())
        return response
    }

    private fun getMockedCompletedChallenges2(): LiveData<BaseApiResponse<ChallengesListResponse>> {
        val response = MutableLiveData<BaseApiResponse<ChallengesListResponse>>()
        val challenge = ChallengesListResponse(totalPages = 1, totalItems = null
            , data = mutableListOf(ChallengeResponse(id = "13", name = "Ice Crown Citadel")))
        response.value = BaseApiResponse.create(Response.success(challenge))
        return response
    }

    private fun getMockedAuthoredChallenges2(): LiveData<BaseApiResponse<ChallengesListResponse>> {
        val response = MutableLiveData<BaseApiResponse<ChallengesListResponse>>()
        val challenge = ChallengesListResponse(totalPages = 1, totalItems = null
            , data = mutableListOf(ChallengeResponse(id = "11", name = "Black Temple")))
        response.value = BaseApiResponse.create(Response.success(challenge))
        return response
    }

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

}