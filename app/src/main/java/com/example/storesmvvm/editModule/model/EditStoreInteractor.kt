package com.example.storesmvvm.editModule.model

import com.example.storesmvvm.StoreApplication
import com.example.storesmvvm.common.entities.StoreEntity
import java.util.concurrent.LinkedBlockingQueue

/**
 * This class is used to manage the store's data. In this case add or update a store in database
 * ( is the model in MVVM)
 */
class EditStoreInteractor {

    /**
     * Save a new store in database
     *@return Long newId (callback: (Long)), but return as parameter
     */
    fun saveStore(storeEntity: StoreEntity, callback: (Long) -> Unit ) {
        val queue = LinkedBlockingQueue<Long>()
        Thread{
            val newId = StoreApplication.database.storeDao().addStore(storeEntity)
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(newId)
        }.start()
        callback(queue.take())
    }


    /**
     * update store in database
     *@return Long newId (callback: (Long)), but return as parameter
     */
    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit ) {
        val queue = LinkedBlockingQueue<StoreEntity>()

        Thread {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)

        }.start()

        callback(queue.take())

    }


}



















