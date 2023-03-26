package com.example.womensafety.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.womensafety.R
import com.example.womensafety.helpers.Constants

abstract class BaseFragment: Fragment() {
    abstract fun getLayoutRes(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutRes(), container, false)
    }

    fun finish() = requireActivity().finish()

    fun addFragment(fragmentId: String, bundle: Bundle?) {
        getSupportFragmentManager().commit {
            setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
            add(R.id.container, Constants.getFragment(fragmentId), bundle, fragmentId)
            setReorderingAllowed(true)
            addToBackStack(fragmentId)
        }
    }

    fun showToast(str: String) {
        Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
    }

    fun popBackStack() = getSupportFragmentManager().popBackStack()

    private fun getSupportFragmentManager(): FragmentManager = requireActivity().supportFragmentManager

    fun hideKeyboard() {
        val imm: InputMethodManager? =
            requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}