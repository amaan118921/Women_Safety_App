package com.example.womensafety.presentation.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.womensafety.R
import com.example.womensafety.core.ResultState
import com.example.womensafety.data.room.enitities.ContactEntity
import com.example.womensafety.presentation.adapters.ContactAdapter
import com.example.womensafety.helpers.BottomSheetDialog
import com.example.womensafety.helpers.Constants
import com.example.womensafety.helpers.PermissionHelper
import com.example.womensafety.domain.models.ContactModel
import com.example.womensafety.domain.models.toContactModel
import com.example.womensafety.eventbus.ContactEvent
import com.example.womensafety.utils.HelpRepo
import com.example.womensafety.presentation.viewModel.AppViewModel
import com.example.womensafety.utils.Utils
import com.example.womensafety.utils.makeGone
import com.example.womensafety.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(), View.OnClickListener, ContactAdapter.IListener,
    BottomSheetDialog.SBottom, PermissionHelper.IPermission {

    private var permissionHelper: PermissionHelper? = null
    private var adapter: ContactAdapter? = null
    private var contactModel: ContactModel? = null
    private val viewModel: AppViewModel by activityViewModels()
    private lateinit var bottomSheetDialog: BottomSheetDialog

    @Inject
    lateinit var repo: HelpRepo
    override fun getLayoutRes(): Int {
        return R.layout.fragment_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHelper = PermissionHelper(this, requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            finish()
        }
        cvPhoneBook.setOnClickListener(this)
        setupRV()
        getAlertContacts()
        setObservers()
        checkForSMSPermission()
    }

    private fun getAlertContacts() {
        showProgressFrame()
        viewModel.getAlertContacts()
    }

    private fun showProgressFrame() {
        clHome.makeGone()
        pfHome.makeVisible()
    }

    private fun hideProgressFrame() {
        clHome.makeVisible()
        pfHome.makeGone()
    }

    private fun checkForSMSPermission() {
        if (permissionHelper?.isPermissionGranted(
                android.Manifest.permission.SEND_SMS,
                requireContext()
            ) == false
        ) requestPermission(Constants.SMS_REQUEST_CODE)
    }

    private fun setObservers() {
        viewModel.getContactData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Success -> {
                    onSuccessListener(result.data)
                }
                is ResultState.Error -> {
                    onFailureListener(result.message ?: "something, went wrong...")
                }
                is ResultState.Loading -> {
                    onLoadingListener(result.data)
                }
                else ->{}
            }
        }
        viewModel.getUpdateContact().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Success -> {
                    onUpdateContactSuccess(result.data)
                }
                is ResultState.Loading -> {}
                is ResultState.Error -> {
                    onUpdateContactsFailure(result.message)
                }
            }
        }
    }

    private fun onUpdateContactsFailure(message: String?) {
        showToast(message ?: "Couldn't add contact...")
    }

    private fun onUpdateContactSuccess(data: ContactModel?) {
        if (data?.update?.compareTo(Constants.ADD) == 0) {
            Utils.incrementCount(repo)
        } else {
            Utils.decrementCount(repo)
        }

        getAlertContacts()
    }

    private fun onLoadingListener(data: List<ContactEntity>?) {
        adapter?.bindList(data as MutableList<ContactEntity>)
    }

    private fun onFailureListener(message: String) {
        showToast(message)
        hideProgressFrame()
    }

    private fun onSuccessListener(data: List<ContactEntity>?) {
        showToast("Contacts fetched successfully")
        val reqList = lifecycleScope.async(Dispatchers.Default) {
            data?.map { it.toContactModel() }
        }
        runBlocking {
            EventBus.getDefault().post(ContactEvent(Constants.GET_ALERT_CONTACTS, reqList.await()?: emptyList()))
            adapter?.bindList(data as MutableList<ContactEntity>)
            hideProgressFrame()
        }
    }

    private fun setupRV() {
        adapter = ContactAdapter(this, 1)
        rvHome.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cvPhoneBook -> checkForPermission()
        }
    }

    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(Constants.CONTACTS_REQUEST_CODE)
        } else checkForLimit()
    }

    private fun requestPermission(requestCode: Int) {
        when (requestCode) {
            Constants.CONTACTS_REQUEST_CODE -> requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                requestCode
            )
            Constants.SMS_REQUEST_CODE -> requestPermissions(
                arrayOf(android.Manifest.permission.SEND_SMS),
                requestCode
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper?.processPermissions(requestCode, permissions, grantResults)
    }

    private fun toContactsFragment() {
        addFragment(Constants.CONTACTS_FRAGMENT, null)
    }

    override fun onItemClick(model: ContactModel) {
        this.contactModel = model
        showBottomSheet(model.name)
    }

    private fun showBottomSheet(confirmation: String) {
        bottomSheetDialog = BottomSheetDialog(requireContext(), this, confirmation)
        bottomSheetDialog.initDelete()
    }

    override fun onNoClick() {
        bottomSheetDialog.dismiss()
    }

    override fun onYesClick() {
        contactModel?.let { viewModel.deleteContact(it._id, repo) }
        bottomSheetDialog.dismiss()
    }

    override fun onPermissionGranted(requestCode: Int, permission: String) {
        when (requestCode) {
            Constants.CONTACTS_REQUEST_CODE -> checkForLimit()
            Constants.SMS_REQUEST_CODE -> showToast("SMS permission granted")
        }
    }

    private fun checkForLimit() {
        val countString = repo.getSharedPreferences(Constants.CONTACTS_LIMIT)
        if (countString.isNotEmpty() && countString.toInt() == 3) {
            showToast("Cannot add more than 3 contacts...")
            return
        }
        toContactsFragment()
    }

    override fun onPermissionDenied(requestCode: Int, permission: String) {
        showToast("Permission denied...")
    }

    override fun onPermissionDeniedPermanently(requestCode: Int, permission: String) {
        showToast("Permissions denied permanently, go to app settings to change permissions")
    }

}