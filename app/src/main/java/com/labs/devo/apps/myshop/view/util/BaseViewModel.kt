package com.labs.devo.apps.myshop.view.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Base view model which can be used by all other view models.
 */
open class BaseViewModel<ChannelState>: ViewModel() {

    protected val channel: Channel<ChannelState> = Channel()

    /**
     * Public channel
     */
    val channelFlow = channel.receiveAsFlow()




}