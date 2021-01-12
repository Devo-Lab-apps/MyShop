package com.labs.devo.apps.myshop.view.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

open class BaseViewModel<ChannelState>: ViewModel() {

    protected val channel: Channel<ChannelState> = Channel()

    var channelFlow = channel.receiveAsFlow()




}