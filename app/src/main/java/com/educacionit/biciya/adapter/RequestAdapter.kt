package com.educacionit.biciya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.educacionit.biciya.R
import com.educacionit.biciya.data.database.RequestEntity

class RequestAdapter(private var requests: List<RequestEntity>) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExpirationDate: TextView = view.findViewById(R.id.tvExpirationDate)
        val tvRange: TextView = view.findViewById(R.id.tvRange)
        val tvBikeCount: TextView = view.findViewById(R.id.tvBikeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)

    }

    override fun onBindViewHolder(
        holder: RequestViewHolder,
        position: Int
    ) {
        val request = requests[position]
        val context = holder.itemView.context
        holder.tvExpirationDate.text =
            context.getString(R.string.expiration_date_requests, request.expirationDate)
        holder.tvRange.text =
            context.getString(R.string.range_requests, request.distanceRange.toString())
        holder.tvBikeCount.text =
            context.getString(R.string.bikes_requests, request.bikesRequested.toString())
    }

    override fun getItemCount() = requests.size

    fun updateData(newList: List<RequestEntity>) {
        requests = newList
        notifyDataSetChanged()
    }

}