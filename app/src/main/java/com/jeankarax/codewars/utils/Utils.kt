package com.jeankarax.codewars.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Utils {

    fun isOnline(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null){
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            capabilities?.let {
                if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    return true
                }else if(it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    return true
                }else if(it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    return true
                }
            }
        }
        return false
    }

}