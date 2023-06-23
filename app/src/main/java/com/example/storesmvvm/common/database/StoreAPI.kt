package com.example.storesmvvm.common.database

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class StoreAPI constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: StoreAPI? = null

        //singleton instance
        fun getInstance(context: Context) = INSTANCE?: synchronized(this){
            INSTANCE ?: StoreAPI(context).also { INSTANCE = it }
        }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }


    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }
}































