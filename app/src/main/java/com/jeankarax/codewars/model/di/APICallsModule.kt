package com.jeankarax.codewars.model.di

import com.jeankarax.codewars.model.api.APICalls
import com.jeankarax.livedataretrofitadapterlibrary.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
open class APICallsModule {

    private val BASE_URL = "https://www.codewars.com/api/v1/"

    @Provides
    fun providesAPICalls(): APICalls {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(APICalls::class.java)
    }

}