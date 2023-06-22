package com.example.storesmvvm.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storesmvvm.common.entities.StoreEntity
import com.example.storesmvvm.mainModule.model.MainInteractor


/**
 * This class is going to be observed from the view.
 */
class MainViewModel: ViewModel() {

    private var storeListAux: MutableList<StoreEntity>
    private var interactor: MainInteractor

    init {
        storeListAux = mutableListOf()
        interactor = MainInteractor()
    }

    //lazy initialization
    private val storesList: MutableLiveData<List<StoreEntity>> by lazy {
        MutableLiveData<List<StoreEntity>>()/*.also {
            loadStores()
        }*/
    }


    fun getStores(): LiveData<List<StoreEntity>> {

        return storesList.also { loadStores() }
    }


    /***
     * Get all stores in database.
     */
    private fun loadStores() {

//        interactor.getStoresCallback(object : MainInteractor.StoresCallback{
//            override fun getStoresCallback(stores: MutableList<StoreEntity>) {
//                storesList.value = stores
//            }
//        })

        //it the same that the previous code
        interactor.getStores {
            storesList.value = it
            storeListAux = it
        }


    }


    /**
     * Delete a store from database. Call the interactor and give it the store.
     * @param storeEntity store to delete
     */
    fun deleteStore(storeEntity: StoreEntity){

        interactor.deleteStore(storeEntity) {
            val index = storeListAux.indexOf(storeEntity)
            if (index != -1) {
                storeListAux.removeAt(index)
                storesList.value = storeListAux
            }
        }
    }



    /**
     * Update a store. Call the interactor and give it the store.
     * @param storeEntity store to update
     */
    fun updateStore(storeEntity: StoreEntity){

        storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.updateStore(storeEntity) {
            val index = storeListAux.indexOf(storeEntity)
            if (index != -1){
                storeListAux[index] = storeEntity
                storesList.value = storeListAux
            }
        }
    }











}



































