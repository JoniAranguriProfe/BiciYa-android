package com.educacionit.biciya.utils.popup

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.educacionit.biciya.R
import com.educacionit.biciya.home.contracts.request.presenter.RequestPresenter
import com.educacionit.biciya.utils.Constants
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PopUpManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {

    companion object {
        val MINIMUM_BIKES_REQUEST = 1
        val MAXIMUM_BIKES_REQUEST = 20
        val DATE_FORMAT = Constants.DATE_FORMAT
        val DEFAULT_DISTANCE = 100
    }

    fun showAddRequestDialog(presenter: RequestPresenter) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_add_request, null)
        val etExpirationDate: EditText = dialogView.findViewById(R.id.etExpirationDate)
        val rsSearch: RangeSlider = dialogView.findViewById(R.id.rsSearch)
        val spnBikes: Spinner = dialogView.findViewById(R.id.spnBikes)
        val btnRequest: Button = dialogView.findViewById(R.id.btnRequets)


        //Deadline date and time selector
        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        etExpirationDate.setText(sdf.format(now.time))
        etExpirationDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val datePickerDialog = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val timePickerDialog = android.app.TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)

                            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                            etExpirationDate.setText(sdf.format(calendar.time))
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    )
                    timePickerDialog.show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }

        //Bicycle quantity selector
        val numbers = (MINIMUM_BIKES_REQUEST..MAXIMUM_BIKES_REQUEST).toList()
        val adapter = ArrayAdapter(context, R.layout.item_spinner, numbers)
        adapter.setDropDownViewResource(R.layout.item_spinner)
        spnBikes.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        btnRequest.setOnClickListener {
            val expiration = etExpirationDate.text.toString()
            if (expiration.isEmpty()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_expiration_date_message),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val distance = rsSearch.values.firstOrNull()?.toInt() ?: DEFAULT_DISTANCE
            val bikes = spnBikes.selectedItem as Int

            lifecycleOwner.lifecycleScope.launch {
                presenter.saveRequest(expiration, distance, bikes)
            }
            dialog.dismiss()
        }
    }
}