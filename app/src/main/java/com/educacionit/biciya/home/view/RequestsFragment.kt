package com.educacionit.biciya.home.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.educacionit.biciya.R
import com.educacionit.biciya.home.view.adapter.RequestAdapter
import com.educacionit.biciya.home.model.RequestRepository
import com.educacionit.biciya.data.database.AppDatabase
import com.educacionit.biciya.data.database.RequestEntity
import com.educacionit.biciya.home.contracts.request.RequestView
import com.educacionit.biciya.home.contracts.request.presenter.RequestPresenterImpl
import com.educacionit.biciya.utils.notification.NotificationHelper
import com.educacionit.biciya.utils.notification.NotificationPermissionManager
import com.educacionit.biciya.utils.popup.PopUpManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [RequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestsFragment : Fragment(), RequestView {

    private lateinit var fab: FloatingActionButton
    private lateinit var presenter: RequestPresenterImpl
    private lateinit var rvRequests: RecyclerView
    private lateinit var adapter: RequestAdapter
    private lateinit var frameProgress: FrameLayout
    private lateinit var tvExpirationDate: TextView
    private lateinit var tvRange: TextView
    private lateinit var tvBikeCount: TextView
    private lateinit var frameNoData: FrameLayout
    private lateinit var frameNoActiveRequest : FrameLayout
    private lateinit var frameNoInactiveData: FrameLayout


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                NotificationHelper.showRequestSavedNotification(requireContext())
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_notification_permission_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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
        frameProgress = view.findViewById(R.id.frameProgress)
        fab = view.findViewById(R.id.fab)
        rvRequests = view.findViewById(R.id.rvRequests)
        tvExpirationDate = view.findViewById(R.id.tvExpirationDate)
        tvRange = view.findViewById(R.id.tvRange)
        tvBikeCount = view.findViewById(R.id.tvBikeCount)
        frameNoData = view.findViewById(R.id.frameNoData)
        frameNoInactiveData = view.findViewById(R.id.frameNoInactiveData)
        frameNoActiveRequest = view.findViewById(R.id.frameNoActiveRequest)

        initRecyclerAdapter()
    }

    override fun initPresenter() {
        val dao = AppDatabase.getInstance(requireContext()).requestDao()
        val repo = RequestRepository(dao)
        presenter = RequestPresenterImpl(view = this, repository = repo, context = requireContext())
    }

    override fun setLoadingVisibility(isVisible: Boolean) {
        frameProgress.isVisible = isVisible
    }

    override fun setListenerViews() {
        fab.setOnClickListener {
            showPopupAddRequest()
        }
    }

    override fun onRequestSaved() {
        requestNotificationPermissionIfNeeded()
        getRequests()
    }

    override fun onRequestError(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onInactiveRequestsLoaded(requests: List<RequestEntity>) {
        adapter.updateData(requests)
    }

    override fun onActiveRequestLoaded(request: RequestEntity) {
        tvExpirationDate.text = getString(R.string.expiration_date_requests, request.expirationDate)
        tvRange.text = getString(R.string.range_requests, request.distanceRange.toString())
        tvBikeCount.text = getString(R.string.bikes_requests, request.bikesRequested.toString())
    }

    override fun setNoRequestsVisibility(isVisible: Boolean) {
        Log.e("setNoRequestsVisibility", "frameNoData isvisible = $isVisible")
        frameNoData.isVisible = isVisible
    }

    override fun setNoActiveRequestsVisibility(isVisible: Boolean) {
        Log.e("setNoActiveRequestsVisibility", "frameNoActiveRequest isvisible = $isVisible")
        frameNoActiveRequest.isVisible = isVisible
    }

    override fun setInactivesRequestsVisibility(isVisible: Boolean) {
        Log.e(
            "setInactivesRequestsVisibility",
            "frameNoInactiveData isvisible = $isVisible"
        )
        frameNoInactiveData.isVisible = isVisible
    }

    private fun showPopupAddRequest() {
        PopUpManager(requireContext(), this).showAddRequestDialog(presenter)
    }

    private fun initRecyclerAdapter() {
        adapter = RequestAdapter(emptyList())
        rvRequests.layoutManager = LinearLayoutManager(requireContext())
        rvRequests.adapter = adapter
    }

    private fun getRequests() {
        lifecycleScope.launch {
            presenter.getActiveRequest()
            presenter.getInactiveRequests()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationPermissionManager.hasNotificationPermission(requireContext())) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationHelper.showRequestSavedNotification(requireContext())
            }
        } else {
            NotificationHelper.showRequestSavedNotification(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()
        getRequests()
    }
}