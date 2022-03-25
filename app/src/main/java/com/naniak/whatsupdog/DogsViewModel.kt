package com.naniak.whatsupdog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naniak.whatsupdog.api.BaseViewModel
import com.naniak.whatsupdog.model.DogRandomResponse
import kotlinx.coroutines.launch

class DogsViewModel() : BaseViewModel() {

    val dogRepository = DogsRepository()

    private val _onSuccessDogRandom = MutableLiveData<DogRandomResponse>()
    val onSuccessDogRandom: LiveData<DogRandomResponse> get() = _onSuccessDogRandom

    fun getRandomDogs(){
        viewModelScope.launch {
            this@DogsViewModel.callApi(
                call = {dogRepository.getRandomDogs()},
                onSuccess = {_onSuccessDogRandom.postValue(it as DogRandomResponse)}
            )
        }
    }

}