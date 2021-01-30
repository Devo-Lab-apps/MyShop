package com.labs.devo.apps.myshop.view.util

/**
 * Class to hold the state of a event.
 * @param message if there is any message for UI
 * @param data if there is any data for UI
 * @param loading if the state is to be loaded
 */
data class DataState<T>(
    var message: Event<String>? = null,
    var loading: Boolean = false,
    var data: Event<T>? = null
) {
    companion object {

        fun <T> message(
            message: String
        ): DataState<T> {
            return DataState(
                message = Event(message),
                loading = false,
                data = null
            )
        }

        fun <T> loading(
            isLoading: Boolean
        ): DataState<T> {
            return DataState(
                message = null,
                loading = isLoading,
                data = null
            )
        }

        fun <T> data(
            data: T? = null,
            message: String? = null
        ): DataState<T> {
            return DataState(
                message = Event.messageEvent(message),
                loading = false,
                data = Event.dataEvent(data)
            )
        }
    }

    override fun toString(): String {
        return "DataState(message=$message,loading=$loading,data=$data)"
    }
}