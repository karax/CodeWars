package com.jeankarax.codewars.model.api

import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.BaseApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type): CallAdapter<R, LiveData<BaseApiResponse<R>>> {


    override fun responseType() = responseType

    override fun adapt(call: Call<R>): LiveData<BaseApiResponse<R>> {
        return object : LiveData<BaseApiResponse<R>>(){
            private var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()

                if (started.compareAndSet(false, true)){
                    call.enqueue(object : Callback<R>{
                        override fun onFailure(call: Call<R>, t: Throwable) {
                            postValue(BaseApiResponse.create(t))
                        }

                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(BaseApiResponse.create(response))
                        }

                    })
                }
            }
        }
    }


}