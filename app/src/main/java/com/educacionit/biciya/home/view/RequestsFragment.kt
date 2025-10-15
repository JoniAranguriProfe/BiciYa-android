package com.educacionit.biciya.home.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.educacionit.biciya.R
import com.educacionit.biciya.home.contracts.request.RequestView
import com.educacionit.biciya.utils.popup.PopUpManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider


/**
 * A simple [Fragment] subclass.
 * Use the [RequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestsFragment : Fragment(), RequestView {

    private lateinit var fab : FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requests, container, false)

        initView(view)
        initPresenter()
        setListenerViews()

        return view
    }

    override fun initView(view: View) {
        fab = view.findViewById(R.id.fab)
    }

    override fun initPresenter() {
        Log.i("initPresenter", "initPresenter")
    }

    override fun setLoadingVisibility(isVisible: Boolean) {
        Log.i("setLoadingVisibility", "setLoadingVisibility")
    }

    override fun setListenerViews() {
        fab.setOnClickListener {
            showPopupAddRequest()
        }
    }

    fun showPopupAddRequest(){
        val popupManager = PopUpManager(requireContext())
        popupManager.showAddRequestDialog()
    }
}