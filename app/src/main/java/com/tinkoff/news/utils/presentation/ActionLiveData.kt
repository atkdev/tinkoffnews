package com.tinkoff.news.utils.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

class ActionLiveData<T> : MutableLiveData<T>() {

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<T?>) {

        if (hasObservers()) {
            throw Throwable("Only one observer at a time may subscribe to a ActionLiveData")
        }

        super.observe(owner, Observer { data ->

            if (data == null) return@Observer
            observer.onChanged(data)
            value = null
        })
    }

    // Just a nicely named method that wraps setting the value
    @MainThread
    fun sendAction(data: T) {
        value = data
    }
}