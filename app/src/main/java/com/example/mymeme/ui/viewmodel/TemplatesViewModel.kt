package com.example.mymeme.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymeme.data.entities.ImgFlipTemplateResponse
import com.example.mymeme.data.entities.MemeGenTemplateResponse
import com.example.mymeme.other.Resource
import com.example.mymeme.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplatesViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _getMemeGenTemplates = MutableLiveData<Resource<MemeGenTemplateResponse>>()
    val getMemeGenTemplates: LiveData<Resource<MemeGenTemplateResponse>> = _getMemeGenTemplates

    fun loadMemeGenTemplates() {
        _getMemeGenTemplates.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getMemeGenTemplates()
            _getMemeGenTemplates.postValue(result)
        }
    }

    private val _getImgFlipTemplates = MutableLiveData<Resource<ImgFlipTemplateResponse>>()
    val getImgFlipTemplates: LiveData<Resource<ImgFlipTemplateResponse>> = _getImgFlipTemplates

    fun loadImgFlipTemplates() {
        _getImgFlipTemplates.postValue(Resource.Loading())
        viewModelScope.launch {
            val result = repository.getImgFlipTemplates()
            _getImgFlipTemplates.postValue(result)
        }
    }

}