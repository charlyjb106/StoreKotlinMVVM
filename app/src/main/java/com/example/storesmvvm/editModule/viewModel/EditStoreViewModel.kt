package com.example.storesmvvm.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storesmvvm.common.entities.StoreEntity
import com.example.storesmvvm.editModule.model.EditStoreInteractor

/**
 * ViewModel from EditStoreFragment(MVVM)
 * This class is used to pass information between  model and view and vice versa
 */
class EditStoreViewModel: ViewModel() {

    private val storeSelected = MutableLiveData<StoreEntity>()
    private val showFab =  MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()

    private val interactor: EditStoreInteractor

    init {
        interactor = EditStoreInteractor()
    }


    fun setStoreSelected(storeEntity: StoreEntity) {

        storeSelected.value = storeEntity
    }

    fun getStoreSelected(): LiveData<StoreEntity> {
        return storeSelected
    }

    fun setshowFab(isVisible: Boolean) {

        showFab.value = isVisible
    }

    fun getshowFab(): LiveData<Boolean> {
        return showFab
    }


    fun setResult(value: Any) {

        result.value = value
    }

    fun getResult(): LiveData<Any> {
        return result
    }


    fun saveStore(storeEntity: StoreEntity){
        interactor.saveStore(storeEntity) { newId ->
            result.value = newId
        }
    }

    fun updateStore(storeEntity: StoreEntity) {
        interactor.updateStore(storeEntity) { storeUpdated ->
            result.value = storeUpdated
        }
    }



}



































