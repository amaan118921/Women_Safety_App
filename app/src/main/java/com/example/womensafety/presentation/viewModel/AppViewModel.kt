package com.example.womensafety.presentation.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.womensafety.core.ResultState
import com.example.womensafety.core.utils.await
import com.example.womensafety.data.room.enitities.ContactEntity
import com.example.womensafety.presentation.application.MyApplication
import com.example.womensafety.domain.repositories.AppRepository
import com.example.womensafety.eventbus.MessageEvent
import com.example.womensafety.helpers.Constants
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.Contacts
import com.example.womensafety.domain.usecase.ContactsUseCase
import com.example.womensafety.utils.HelpRepo
import com.example.womensafety.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val contactsUseCase: ContactsUseCase,
    application: MyApplication
) : AndroidViewModel(application) {
    private var auth = FirebaseAuth.getInstance()
    private var _contact = MutableLiveData<ResultState<List<ContactEntity>>?>()
    private var _updateContact = MutableLiveData<ResultState<ContactModel>>()
    fun getContactData(): LiveData<ResultState<List<ContactEntity>>?> {
        return _contact
    }
    fun getUpdateContact(): LiveData<ResultState<ContactModel>> {
        return _updateContact
    }

    @Inject
    lateinit var appRepository: AppRepository

    fun addContactToAlert(contactModel: ContactModel, repo: HelpRepo) {
        viewModelScope.launch {
            val response = appRepository.addAlertContacts(contactModel).await()
            _updateContact.postValue(
                response
            )
        }
    }

    fun deleteContact(_id: String, repo: HelpRepo) {
        viewModelScope.launch {
            val response = appRepository.deleteContact(_id, auth.currentUser?.uid.toString()).await()
            _updateContact.postValue(
                response
            )
        }
    }

    fun getAlertContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            auth.currentUser?.uid?.let {
                _contact.postValue(
                    contactsUseCase(it)
                )
            }
        }
    }

    private fun getContext() = getApplication<MyApplication>().applicationContext
}