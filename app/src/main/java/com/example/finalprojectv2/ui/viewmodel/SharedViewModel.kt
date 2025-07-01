package com.example.finalprojectv2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectv2.ui.search.Event

class SharedViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    fun postEvents(eventList: List<Event>) {
        _events.value = eventList
    }

}