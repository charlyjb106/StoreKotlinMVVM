package com.example.storesmvvm.mainModule.adapter

import com.example.storesmvvm.common.entities.StoreEntity


/**
 * interface that is made to override the OnclickListener, to adapt it to our specific behaviour
 */
interface OnClickListener {
    fun onClick(storeEntity: StoreEntity)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}