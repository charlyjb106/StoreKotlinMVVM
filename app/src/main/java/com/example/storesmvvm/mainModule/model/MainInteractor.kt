package com.example.storesmvvm.mainModule.model

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.storesmvvm.StoreApplication
import com.example.storesmvvm.common.entities.StoreEntity
import com.example.storesmvvm.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.LinkedBlockingQueue

/**
 * VIEMMODEL for the main activity
 *
 * https://stores.free.beeceptor.com/my/api/stores (api url)
 */
class MainInteractor {


    /**
     * Get all stores from API rest using Volley
     */
    fun getStores( callback: (MutableList<StoreEntity>) -> Unit){

        val url = Constants.STORES_URL + Constants.GET_ALL_PATH

        var storeList = mutableListOf<StoreEntity>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {response ->

            //val status = response.getInt(Constans.STATUS_PROPERTY)
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)

            //if the status property in response = Success, means that it has not errors
            if(status == Constants.SUCCESS) {

                //get 1 instance
/*                val jsonObject = Gson().fromJson(response.getJSONArray(Constans.STORES_PROPERTY)
                    .get(0).toString(), StoreEntity::class.java)*/

                //json to list
                val jsonList = response.optJSONArray(Constants.STORES_PROPERTY)?.toString()

                //if get the property "stores" from response
                if (jsonList != null){
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                    storeList = Gson().fromJson<MutableList<StoreEntity>>(jsonList, mutableListType)

                    callback(storeList)
                    return@JsonObjectRequest //avoid call callback again in the next line
                }

            }

            callback(storeList) //call callback if it is not success

        }, {
            it.printStackTrace()
            callback(storeList)
        })
        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)

    }





    /**
     * NOT IN USE,  because now get stores from api rest instead of room database
     * Get the stores
     * higher order function
     * (tipo de dato a devolver) -> Unit (unit es que no devuelve nada de forma externa)
     * en este caso storeslist lo devuelve como parametro de la funcion
     */
    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit){
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread{
            val storeList = StoreApplication.database.storeDao().getAllStores()
            queue.add(storeList)
        }.start()

        callback(queue.take())
    }


    /**
     * Delete a store from datbase
     * @param storeEntity store to delete
     * @param callback
     */
    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit ) {
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        callback(queue.take())
    }



    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit) {
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        callback(queue.take())
    }


}





































