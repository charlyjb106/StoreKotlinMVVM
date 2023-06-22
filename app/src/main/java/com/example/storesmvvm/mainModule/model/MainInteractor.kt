package com.example.storesmvvm.mainModule.model

import com.example.storesmvvm.StoreApplication
import com.example.storesmvvm.common.entities.StoreEntity
import java.util.concurrent.LinkedBlockingQueue

class MainInteractor {


    /**
     * Get the stores
     * higher order function
     * (tipo de dato a devolver) -> Unit (unit es que no devuelve nada de forma externa)
     * en este caso storeslist lo devuelve como parametro de la funcion
     */
    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
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





































