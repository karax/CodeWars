package com.jeankarax.codewars

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeankarax.codewars.model.api.APICalls
import com.jeankarax.codewars.model.api.ChallengeAPI
import com.jeankarax.codewars.model.api.UserAPI
import com.jeankarax.codewars.model.response.ChallengeResponse
import com.jeankarax.codewars.model.response.ChallengesListResponse
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
    fun whenGetUser_andIsResponseError_thenReturnUserError(){
        val userAPI = UserAPI(apiCalls)
        val mockedUser: Single<UserResponse> = getMockedUserError()
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

    @Test
    fun whenGetChallenge_andIsResponseError_thenReturnChallengeError(){
        val challengeAPI = ChallengeAPI(apiCalls)
        var isLoading = true
        var isError = false

        Mockito.`when`(apiCalls.getChallenge("12")).thenReturn(getMockedChallengeError())

        fun getChallengeResponse() {
            challengeAPI.getChallenge("12")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengeResponse>() {
                    override fun onSuccess(t: ChallengeResponse) {
                        isLoading = false
                    }
                    override fun onError(e: Throwable) {
                        isLoading = false
                        isError = true
                    }

                })
        }

        getChallengeResponse()
        Assert.assertTrue(isError)
        Assert.assertFalse(isLoading)

    }

    @Test
    fun whenGetChallenge_andIsResponseSuccess_thenReturnChallengeSuccessful(){
        val challengeAPI = ChallengeAPI(apiCalls)
        var isLoading = true
        var testChallengeId = ""

        Mockito.`when`(apiCalls.getChallenge("12")).thenReturn(getMockedChallenge())

        fun getChallengeResponse() {
            challengeAPI.getChallenge("12")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengeResponse>() {
                    override fun onSuccess(t: ChallengeResponse) {
                        isLoading = false
                        testChallengeId = t.id
                    }

                    override fun onError(e: Throwable) {

                    }

                })
        }

        getChallengeResponse()
        Assert.assertEquals(testChallengeId, "12")
        Assert.assertFalse(isLoading)

    }

    @Test
    fun whenGetCompletedChallenges_andIsResponseError_thenReturnCompletedChallengesError(){
        val challengeAPI = ChallengeAPI(apiCalls)
        var isLoading = true
        var isError = false

        Mockito.`when`(apiCalls.getCompletedChallenges("leeeroy", 1)).thenReturn(getMockedCompletedChallengesError())

        fun getChallengeResponse() {
            challengeAPI.getCompletedChallenges("leeeroy", page = 1)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>() {
                    override fun onSuccess(t: ChallengesListResponse) {
                        isLoading = false
                    }
                    override fun onError(e: Throwable) {
                        isLoading = false
                        isError = true
                    }

                })
        }

        getChallengeResponse()
        Assert.assertTrue(isError)
        Assert.assertFalse(isLoading)

    }

    @Test
    fun whenGetCompletedChallenge_andIsResponseSuccess_thenReturnCompletedChallengesSuccessful(){
        val challengeAPI = ChallengeAPI(apiCalls)
        var isLoading = true
        var testFirstChallengeId = ""

        Mockito.`when`(apiCalls.getCompletedChallenges(userName, page = 1)).thenReturn(getMockedCompletedChallenges())

        fun getChallengeResponse() {
            challengeAPI.getCompletedChallenges(userName, page = 1)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>() {
                    override fun onSuccess(t: ChallengesListResponse) {
                        isLoading = false
                        testFirstChallengeId = t.data?.get(0)?.id.toString()
                    }

                    override fun onError(e: Throwable) {

                    }

                })
        }

        getChallengeResponse()
        Assert.assertEquals(testFirstChallengeId, "13")
        Assert.assertFalse(isLoading)

    }

    @Test
    fun whenGetAuthoredChallenges_andIsResponseError_thenReturnAuthoredChallengesError(){
        val challengeAPI = ChallengeAPI(apiCalls)
        var isLoading = true
        var isError = false

        Mockito.`when`(apiCalls.getAuthoredChallenges(userName)).thenReturn(getMockedAuthoredChallengesError())

        fun getChallengeResponse() {
            challengeAPI.getAuthoredChallenges(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>() {
                    override fun onSuccess(t: ChallengesListResponse) {
                        isLoading = false
                    }
                    override fun onError(e: Throwable) {
                        isLoading = false
                        isError = true
                    }

                })
        }

        getChallengeResponse()
        Assert.assertTrue(isError)
        Assert.assertFalse(isLoading)

    }

    @Test
    fun whenGetAuthoredChallenges_andIsResponseSuccess_thenReturnChallengesSuccessful(){
        val challengeAPI = ChallengeAPI(apiCalls)
        var isLoading = true
        var testFirstChallengeId = ""

        Mockito.`when`(apiCalls.getAuthoredChallenges(userName)).thenReturn(getMockedAuthoredChallenges())

        fun getChallengeResponse() {
            challengeAPI.getAuthoredChallenges(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ChallengesListResponse>() {
                    override fun onSuccess(t: ChallengesListResponse) {
                        isLoading = false
                        testFirstChallengeId = t.data?.get(0)?.id.toString()
                    }

                    override fun onError(e: Throwable) {

                    }

                })
        }

        getChallengeResponse()
        Assert.assertEquals(testFirstChallengeId, "13")
        Assert.assertFalse(isLoading)

    }



    private fun getMockedSuccessUser(): Single<UserResponse> = Single
        .just(UserResponse(username = "leeroy", name = "Leroy Jenkins"))

    private fun getMockedUserError(): Single<UserResponse> = Single.error(Throwable())

    private fun getMockedChallenge(): Single<ChallengeResponse> = Single
        .just(ChallengeResponse(id = "12", name = "Black Temple"))

    private fun getMockedChallengeError(): Single<ChallengeResponse> = Single.error(Throwable())

    private fun getMockedCompletedChallenges(): Single<ChallengesListResponse>? =
        Single.just(ChallengesListResponse(totalPages = 1, totalItems = null
            , data = listOf(ChallengeResponse(id = "13", name = "Ice Crown Citadel"))))

    private fun getMockedCompletedChallengesError(): Single<ChallengesListResponse>?  = Single.error(Throwable())

    private fun getMockedAuthoredChallenges(): Single<ChallengesListResponse>? =
        Single.just(ChallengesListResponse(totalPages = 1, totalItems = null
            , data = listOf(ChallengeResponse(id = "13", name = "Ice Crown Citadel"))))

    private fun getMockedAuthoredChallengesError(): Single<ChallengesListResponse>?  = Single.error(Throwable())

}