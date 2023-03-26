package com.example.womensafety.presentation.fragments

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.text.bold
import com.example.womensafety.R
import com.example.womensafety.helpers.Constants
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: BaseFragment(), TextWatcher {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_login
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            finish()
        }
        btnLogin.setOnClickListener {
           validate(etPhone.text.trim().toString())
        }
        etPhone.addTextChangedListener(this)
        val str = SpannableStringBuilder().apply {
            append("We will send you ")
                .bold {
                    append("One Time Password")
                }
            append(" on this mobile number")
        }
        tvInfo.text = str
    }

    private fun validate(phone: String) {
        if(phone.length<10) {
            showToast("Please enter valid phone number...")
            return
        }
        hideKeyboard()
        toOtpFragment(phone)
    }

    private fun toOtpFragment(phone: String) {
        Bundle().apply {
            putString(Constants.PHONE, phone)
            addFragment(Constants.OTP_FRAGMENT, this)
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(e: Editable?) {
        update(e?.length)
    }

    private fun update(count: Int?) {
        tvCount.text = "$count"
    }
}