package com.labs.devo.apps.myshop.view.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BaseViewModel<ChannelState>: ViewModel() {

    private val channel: Channel<ChannelState> = Channel()

    var channelFlow = channel.receiveAsFlow()




}