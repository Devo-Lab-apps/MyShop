package com.labs.devo.apps.myshop.view.util


/**
 * Interface to communicate between activity and fragments.
 */
interface DataStateListener {

    /**
     * Called when data state of fragment is changed.
     */
    fun onDataStateChange(dataState: DataState<*>?)
}