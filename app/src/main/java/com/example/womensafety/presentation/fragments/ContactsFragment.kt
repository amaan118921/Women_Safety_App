package com.example.womensafety.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.womensafety.R
import com.example.womensafety.data.room.enitities.ContactEntity
import com.example.womensafety.presentation.adapters.ContactAdapter
import com.example.womensafety.helpers.BottomSheetDialog
import com.example.womensafety.helpers.Constants
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.Contacts
import com.example.womensafety.domain.models.toContactEntity
import com.example.womensafety.utils.HelpRepo
import com.example.womensafety.utils.Utils
import com.example.womensafety.presentation.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.coroutines.*
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : BaseFragment(), ContactAdapter.IListener, BottomSheetDialog.SBottom,
    TextWatcher {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var adapter: ContactAdapter? = null
    private var job: Job? = null
    private var newJob: Job? = null
    private var contactModel: ContactModel? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val viewModel: AppViewModel by activityViewModels()
    private var contactList = arrayListOf<ContactModel>()

    @Inject
    lateinit var repo: HelpRepo

    override fun getLayoutRes(): Int {
        return R.layout.fragment_contacts
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            popBackStack()
        }
        setupRV()
        getContacts()
        etSearch.addTextChangedListener(this)
        ivBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun setupRV() {
        adapter = ContactAdapter(this, 0)
        rvContacts.adapter = adapter
    }

    @SuppressLint("Range")
    private fun getContacts() {
        val contactList = arrayListOf<ContactModel>()
        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        job = lifecycleScope.launch(Dispatchers.Default) {
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phone =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    ContactModel().apply {
                        this.name = name
                        this.phone = phone
                        contactList.add(this)
                    }
                }
            }
            val list = contactList.map { it.toContactEntity() } as MutableList<ContactEntity>
            withContext(Dispatchers.Main) {
                this@ContactsFragment.contactList = contactList
                adapter?.bindList(list)
                cursor?.close()
            }
        }
    }

    override fun onItemClick(model: ContactModel) {
        this.contactModel = model
        showBottomSheet(model.name)
    }

    private fun showBottomSheet(confirmation: String) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), this, confirmation)
        bottomSheetDialog.initBottom()
    }

    override fun onNoClick() {
        bottomSheetDialog.dismiss()
    }

    override fun onYesClick() {
        contactModel?._id = Calendar.getInstance().timeInMillis.toString()
        contactModel?.userId = auth.currentUser?.uid.toString()
        contactModel?.let { viewModel.addContactToAlert(it, repo) }
        bottomSheetDialog.dismiss()
        popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        newJob?.cancel(null)
        job?.cancel(null)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(e: Editable?) {
        filterList(e?.toString() ?: "")
    }

    private fun filterList(filter: String) {
        val list = arrayListOf<ContactModel>()
        newJob = lifecycleScope.launch(Dispatchers.Default) {
            for (contact: ContactModel in contactList) {
                if (contact.name.contains(filter, true)) {
                    list.add(contact)
                }
            }
            val contactList = list.map { it.toContactEntity() } as MutableList<ContactEntity>
            withContext(Dispatchers.Main) {
                adapter?.bindList(contactList)
            }
        }
    }


}