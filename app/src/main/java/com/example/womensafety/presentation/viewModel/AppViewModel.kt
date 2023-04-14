package com.example.womensafety.presentation.viewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.womensafety.core.ResultState
import com.example.womensafety.core.utils.await
import com.example.womensafety.data.room.enitities.AlertEntity
import com.example.womensafety.data.room.enitities.ContactEntity
import com.example.womensafety.domain.models.AlertModel
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.repositories.AppRepository
import com.example.womensafety.domain.usecase.AddContactUseCase
import com.example.womensafety.domain.usecase.AlertUseCase
import com.example.womensafety.domain.usecase.ContactsUseCase
import com.example.womensafety.domain.usecase.DeleteContactsUseCase
import com.example.womensafety.helpers.Constants
import com.example.womensafety.presentation.application.MyApplication
import com.example.womensafety.utils.HelpRepo
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val contactsUseCase: ContactsUseCase,
    private val addContactUseCase: AddContactUseCase,
    private val deleteContactsUseCase: DeleteContactsUseCase,
    private val alertUseCase: AlertUseCase,
    application: MyApplication
) : AndroidViewModel(application) {

    private var _contact = MutableLiveData<ResultState<List<ContactEntity>>?>()
    private var _updateContact = MutableLiveData<ResultState<ContactModel>?>()
    private var _addContact = MutableLiveData<ResultState<ContactModel>?>()
    private var _deleteContact = MutableLiveData<ResultState<ContactModel>?>()
    private var _alertContact = MutableLiveData<ResultState<List<AlertEntity>>?>()

    private var uid: String? = null


    fun getContactData(): LiveData<ResultState<List<ContactEntity>>?> {
        return _contact
    }

    fun getUpdateContact(): LiveData<ResultState<ContactModel>?> {
        return _updateContact
    }

    fun getAlert(): LiveData<ResultState<List<AlertEntity>>?> {
        return _alertContact
    }

    fun getAddContact(): LiveData<ResultState<ContactModel>?> {
        return _addContact
    }

    fun getDeleteContact(): LiveData<ResultState<ContactModel>?> {
        return _deleteContact
    }

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var repo: HelpRepo


    fun addContactToAlert(contactModel: ContactModel, repo: HelpRepo) {
        viewModelScope.launch(Dispatchers.IO) {
            addContactUseCase(contactModel).onEach {
                _addContact.postValue(
                    it
                )
            }.launchIn(this)
        }
    }

    fun deleteContact(_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uid = repo.getSharedPreferences(Constants.UID)
            if (!uid.isNullOrBlank()) {
                deleteContactsUseCase(_id, uid!!).collect {
                    _deleteContact.postValue(
                        it
                    )
                }
            } else {
                _deleteContact.postValue(
                    ResultState.Error(null, "uid is null....")
                )
            }
        }
    }

    fun getAlertContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            uid = repo.getSharedPreferences(Constants.UID)
            if (!uid.isNullOrBlank()) {
                _contact.postValue(
                    contactsUseCase(uid!!)
                )
            } else {
                _contact.postValue(
                    ResultState.Error(null, "uid is null....")
                )
            }
        }
    }

    fun getAlerts() {
        viewModelScope.launch {
            uid = repo.getSharedPreferences(Constants.UID)
            if (!uid.isNullOrBlank()) {
                _alertContact.postValue(
                    alertUseCase(uid!!)
                )
            } else {
                _contact.postValue(
                    ResultState.Error(null, "uid is null....")
                )
            }
        }
    }

    private fun getContext() = getApplication<MyApplication>().applicationContext
}