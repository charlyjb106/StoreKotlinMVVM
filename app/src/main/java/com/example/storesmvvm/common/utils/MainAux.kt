package com.example.storesmvvm.common.utils

import com.example.storesmvvm.common.entities.StoreEntity


/**
 * This class is not longer used, due to new project structure
 */
interface MainAux {
    fun hideFab(isVisible: Boolean = false)

    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)
}