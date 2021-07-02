package com.example.mymeme.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymeme.data.entities.ItemInfo
import com.example.mymeme.other.Resource
import com.example.mymeme.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
) : ViewModel() {

    private val _getHotTopics = MutableLiveData<Resource<List<ItemInfo>>>()
    val getHotTopics: LiveData<Resource<List<ItemInfo>>> = _getHotTopics

    private val _getInstagramAccounts = MutableLiveData<Resource<List<ItemInfo>>>()
    val getInstagramAccounts: LiveData<Resource<List<ItemInfo>>> = _getInstagramAccounts

    private val _getTelegramChannels = MutableLiveData<Resource<List<ItemInfo>>>()
    val getTelegramChannels: LiveData<Resource<List<ItemInfo>>> = _getTelegramChannels

    private val _getDiscordServers = MutableLiveData<Resource<List<ItemInfo>>>()
    val getDiscordServers: LiveData<Resource<List<ItemInfo>>> = _getDiscordServers

    fun loadHotTopics() {
        _getHotTopics.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getHotTopics()
            _getHotTopics.postValue(result)
        }
    }

    fun loadInstagramAccounts() {
        _getInstagramAccounts.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getInstagramAccounts()
            _getInstagramAccounts.postValue(result)
        }
    }

    fun loadTelegramChannels() {
        _getTelegramChannels.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getTelegramChannels()
            _getTelegramChannels.postValue(result)
        }
    }

    fun loadDiscordServers() {
        _getDiscordServers.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getDiscordServers()
            _getDiscordServers.postValue(result)
        }
    }
}