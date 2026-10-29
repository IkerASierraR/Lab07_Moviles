package com.example.offlinefirstappsierra.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission

object NetworkUtils {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvialable(context: Context): Boolean{
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false

        val network = connectivityManager.activeNetwork ?: return false
        val capabilites = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilites.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilites.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}