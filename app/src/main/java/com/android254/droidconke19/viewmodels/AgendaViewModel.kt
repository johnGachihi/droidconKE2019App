package com.android254.droidconke19.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android254.droidconke19.datastates.FirebaseResult
import com.android254.droidconke19.models.AgendaModel
import com.android254.droidconke19.repository.AgendaRepo
import kotlinx.coroutines.launch

class AgendaViewModel(private val agendaRepo: AgendaRepo) : ViewModel() {
    private val agendaStateMediatorLiveData = MediatorLiveData<List<AgendaModel>>()
    private val agendaError = MediatorLiveData<String>()

    fun getAgendasResponse(): LiveData<List<AgendaModel>> = agendaStateMediatorLiveData

    fun getAgendaError(): LiveData<String> = agendaError

    fun fetchAgendas() {
        viewModelScope.launch {
            when (val value = agendaRepo.agendaData()) {
                is FirebaseResult.Success -> agendaStateMediatorLiveData.postValue(value.data)
                is FirebaseResult.Error -> agendaError.postValue(value.exception)
            }
        }
    }
}
