package com.example.womensafety.helpers

import android.content.Context
import android.view.View
import com.example.womensafety.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView

class BottomSheetDialog(private var context: Context, private val sBottom: SBottom, private val confirmation: String) :
    View.OnClickListener {

    private lateinit var bottomSheetDialog: BottomSheetDialog

    interface SBottom {
        fun onNoClick()
        fun onYesClick()
    }

    fun initBottom() {
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.add_contact_bottom_sheet)
        val btnNo = bottomSheetDialog.findViewById<View>(R.id.btnNo)
        val btnYes = bottomSheetDialog.findViewById<View>(R.id.btnYes)
        val tvConfirm = bottomSheetDialog.findViewById<MaterialTextView>(R.id.tvConfirmation)
        tvConfirm?.text = "Are you sure you want to add $confirmation \n to your alert list?"
        btnNo?.setOnClickListener(this)
        btnYes?.setOnClickListener(this)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
    }

    fun initDelete() {
        bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.add_contact_bottom_sheet)
        val btnNo = bottomSheetDialog.findViewById<View>(R.id.btnNo)
        val btnYes = bottomSheetDialog.findViewById<View>(R.id.btnYes)
        val tvConfirm = bottomSheetDialog.findViewById<MaterialTextView>(R.id.tvConfirmation)
        tvConfirm?.text = "Are you sure you want to remove $confirmation \n from your alert list?"
        btnNo?.setOnClickListener(this)
        btnYes?.setOnClickListener(this)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
    }

    fun dismiss() {
        bottomSheetDialog.dismiss()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnYes -> {
                sBottom.onYesClick()
            }
            R.id.btnNo -> {
                sBottom.onNoClick()
            }
        }
    }
}