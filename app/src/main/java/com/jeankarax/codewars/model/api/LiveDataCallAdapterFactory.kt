package com.jeankarax.codewars.model.api

import androidx.lifecycle.LiveData
import com.jeankarax.codewars.model.response.BaseApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if(getRawType(returnType) != LiveData::class.java){
            return null;
        }

        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)

        val rawObservableType = getRawType(observableType)

        if (rawObservableType != BaseApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }

        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = getParameterUpperBound(0, observableType as ParameterizedType)

        return LiveDataCallAdapter<Any>(bodyType)

    }

}