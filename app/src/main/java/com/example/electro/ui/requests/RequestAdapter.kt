package com.example.electro.ui.requests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electro.data.model.ServiceRequest
import com.example.electro.databinding.ItemRequestBinding

/**
 * Renders a list of [ServiceRequest]s for the customer's "My Requests" screen.
 * Cancel button is shown only when the request is in a cancellable state.
 */
class RequestAdapter(
    private val onCancelClick: (ServiceRequest) -> Unit
) : ListAdapter<ServiceRequest, RequestAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRequestBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onCancelClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemRequestBinding,
        private val onCancelClick: (ServiceRequest) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(req: ServiceRequest) {
            binding.titleText.text = req.title
            binding.categoryText.text = req.category
            binding.statusText.text = req.status.replace('_', ' ').uppercase()
            binding.descriptionText.text = req.description.ifEmpty { "(no description)" }

            val priceLabel = if (req.price > 0) "₹ ${req.price}" else "no budget"
            val addressLabel = req.address.ifEmpty { "(no address)" }
            binding.metaText.text = "Address: $addressLabel · $priceLabel"

            val cancellable = req.status == "pending" || req.status == "accepted"
            binding.cancelButton.visibility =
                if (cancellable) android.view.View.VISIBLE else android.view.View.GONE
            binding.cancelButton.setOnClickListener { onCancelClick(req) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ServiceRequest>() {
            override fun areItemsTheSame(old: ServiceRequest, new: ServiceRequest): Boolean =
                old.id == new.id

            override fun areContentsTheSame(old: ServiceRequest, new: ServiceRequest): Boolean =
                old == new
        }
    }
}
