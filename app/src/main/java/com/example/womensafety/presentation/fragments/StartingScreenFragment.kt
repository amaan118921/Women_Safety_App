package com.example.womensafety.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.womensafety.R
import com.example.womensafety.presentation.adapters.StartingScreenAdapter
import com.example.womensafety.helpers.Constants
import com.example.womensafety.helpers.ScrollListenerHelper
import com.example.womensafety.domain.models.StartingScreenModel
import com.example.womensafety.presentation.services.LocationService
import com.example.womensafety.utils.makeGone
import com.example.womensafety.utils.makeVisible
import kotlinx.android.synthetic.main.fragment_starting_screen.*
import kotlinx.android.synthetic.main.starting_item_view.*

class StartingScreenFragment : BaseFragment(), ScrollListenerHelper.OnSnapPositionChangeListener,
    View.OnClickListener {
    private var pagerSnapHelper: PagerSnapHelper? = null
    private var scrollListenerHelper: ScrollListenerHelper? = null
    private var snapPosition = 0
    private var adapter: StartingScreenAdapter?  = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_starting_screen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            finish()
        }
        setUpRecyclerViewWithPager()
        btnStart.setOnClickListener(this)
    }

    private fun setUpRecyclerViewWithPager() {
        pagerSnapHelper = PagerSnapHelper()
        adapter = StartingScreenAdapter(requireContext())
        adapter?.bindView(getList())
        rvStartingScreen.apply {
            layoutManager =  LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            pagerSnapHelper?.attachToRecyclerView(this)
            adapter = this@StartingScreenFragment.adapter
        }
        pagerSnapHelper?.let { scrollListenerHelper = ScrollListenerHelper(this, it, snapPosition) }
        scrollListenerHelper?.let { rvStartingScreen.addOnScrollListener(it) }
    }

    private fun getList(): ArrayList<StartingScreenModel> {
        val modelOne = StartingScreenModel().apply {
            tv1 = resources.getString(R.string.here_i_am_app)
            tv2 = resources.getString(R.string.she_is_a_woman_a_mother_a_daughter_a_wife_n_and_a_sister_it_s_our_firm_duty_to_protect_women_of_our_society)
            tv3 = resources.getString(R.string.hence_we_have_developed_this_app_to_send_alert_to_our_loved_ones_in_case_of_emergency_harassment_etc)
            img = R.drawable.intro
        }
        val modelTwo = StartingScreenModel().apply {
            tv1 = "Shake to Alert"
            tv2 = "Shake your device to Alert your loved ones in no\n time and be safe!"
            img = R.drawable.shake
        }
        val modelThree = StartingScreenModel().apply {
            tv1 = "Sharing is Caring"
            tv2 = "Share the app with other females around you for\n betterment of our society!"
            img = R.drawable.share
        }
        return arrayListOf(modelOne, modelTwo, modelThree)
    }

    override fun onSnapPositionChange(position: Int) {
        if(position==2) btnStart.makeVisible()
        else btnStart.makeGone()
        manipulateDots(position)
    }

    private fun manipulateDots(position: Int) {
        resetDots()
        when(position) {
            0 -> dot1.setBackgroundResource(R.drawable.re)
            1 -> dot2.setBackgroundResource(R.drawable.re)
            else -> dot3.setBackgroundResource(R.drawable.re)
        }
    }

    private fun resetDots() {
        dot1.setBackgroundResource(R.drawable.rec)
        dot2.setBackgroundResource(R.drawable.rec)
        dot3.setBackgroundResource(R.drawable.rec)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnStart -> goToLogin()
        }
    }

    private fun goToLogin() {
        addFragment(Constants.LOGIN_FRAGMENT, null)
    }


}