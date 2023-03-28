package com.example.womensafety.presentation.fragments


import `in`.aabhasjindal.otptextview.OTPListener
import android.os.Bundle
import android.view.View
import com.example.womensafety.R
import com.example.womensafety.helpers.Constants
import com.example.womensafety.utils.HelpRepo
import com.example.womensafety.utils.makeGone
import com.example.womensafety.utils.makeVisible
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_otp.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class OTPFragment : BaseFragment(), OTPListener {
    private lateinit var auth: FirebaseAuth
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var phone: String? = null
    private var verificationId: String? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_otp
    }

    @Inject
    lateinit var repo: HelpRepo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        phone = arguments?.getString(Constants.PHONE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        val mobile = "+91$phone"
        setCallbacks()
        verifyPhone(mobile)
        etOTP.otpListener = this
        ivBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun verify(otpCode: String) {
        val credential = verificationId?.let { PhoneAuthProvider.getCredential(it, otpCode) }
        showProgressFrame()
        credential?.let { signInWithPhoneAuthCredential(it) }
    }

    private fun showProgressFrame() {
        pfHome.makeVisible()
        clOTP.makeGone()
    }

    private fun hideProgressFrame() {
        pfHome.makeGone()
        clOTP.makeVisible()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    showToast("Verified successfully")
                    if (task.result.user != null) {
                        repo.setSharedPreferences(Constants.LOCAL_PHONE, phone!!)
                        repo.setSharedPreferences(Constants.UID, task.result.user?.uid!!)
                        toHomeFragment()
                    } else {
                        showToast("Entered OTP is incorrect, please try again...")
                        popBackStack()
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showToast("Entered OTP is incorrect, please try again...")
                        popBackStack()
                    }
                }
            }
    }

    private fun verifyPhone(phone: String) {
        val options = phone.let {
            callbacks?.let { it1 ->
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(it)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(it1)
                    .build()
            }
        }
        options?.let { PhoneAuthProvider.verifyPhoneNumber(it) }
    }

    private fun setCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
//                if (e is FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                } else if (e is FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                }
                showToast("Something went wrong, try again later...")
                popBackStack()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@OTPFragment.verificationId = verificationId
            }
        }
    }

    private fun toHomeFragment() {
        addFragment(Constants.HOME_FRAGMENT, null)
    }

    override fun onInteractionListener() {

    }

    override fun onOTPComplete(otp: String) {
        hideKeyboard()
        verify(otp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }
}