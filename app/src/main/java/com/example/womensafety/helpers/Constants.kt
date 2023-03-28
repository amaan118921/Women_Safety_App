package com.example.womensafety.helpers

import androidx.fragment.app.Fragment
import com.example.womensafety.presentation.fragments.*
import kotlinx.android.synthetic.main.fragment_login.*

class Constants {
    companion object {
        const val GET_ALERT_CONTACTS = "GET_ALERT_CONTACTS"
        const val APP_NAME = "com.womensafety"
        const val STARTING_SCREEN = "STARTING_SCREEN"
        const val LOGIN_FRAGMENT = "LOGIN_FRAGMENT"
        const val HOME_FRAGMENT = "HOME_FRAGMENT"
        const val OTP_FRAGMENT = "OTP_FRAGMENT"
        const val CONTACTS_FRAGMENT = "CONTACTS_FRAGMENT"
        const val CONTACTS_REQUEST_CODE = 456669
        const val SMS_REQUEST_CODE = 456668
        const val PHONE = "PHONE"
        const val UID = "UID"
        const val LOCAL_PHONE = "LOCAL_PHONE"
        const val BASE_URL = "https://grumpy-ruby-seagull.cyclic.app/"
        const val LOCATION_CODE = 895311
        const val NOTIFICATION_ID = 45123
        const val CONTACTS_LIMIT = "CONTACTS_LIMIT"
        const val IS_LOGGED_IN = "IS_LOGGED_IN"
        const val ADD = "Add"
        const val DELETE = "Delete"
        fun getFragment(fragmentId: String): Class<Fragment> {
            return when(fragmentId) {
                STARTING_SCREEN -> StartingScreenFragment::class.java as Class<Fragment>
                LOGIN_FRAGMENT -> LoginFragment::class.java as Class<Fragment>
                HOME_FRAGMENT -> HomeFragment::class.java as Class<Fragment>
                CONTACTS_FRAGMENT -> ContactsFragment::class.java as Class<Fragment>
                OTP_FRAGMENT -> OTPFragment::class.java as Class<Fragment>
                else -> LoginFragment::class.java as Class<Fragment>
            }
        }
    }
}