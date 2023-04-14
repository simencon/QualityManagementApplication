package com.simenko.qmapp.repository

import android.os.AsyncTask
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

@MainThread
fun <X, Y> mapAsync(source: LiveData<X>, mapFunction: androidx.arch.core.util.Function<X, Y>): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(source) { x -> AsyncTask.execute { result.postValue(mapFunction.apply(x)) } }
    return result
}