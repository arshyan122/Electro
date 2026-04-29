package com.example.electro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.electro.R
import com.example.electro.data.model.Product
import com.example.electro.databinding.ServicesBinding

/**
 * Renders remote `Product` items into the existing `services.xml` row layout.
 * Used by SearchFragment and the "All Services" bottom sheet.
 */
class RemoteServiceAdapter(
    private val onItemClick: (Product) -> Unit = {}
) : ListAdapter<Product, RemoteServiceAdapter.ServiceViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ServicesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ServiceViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ServiceViewHolder(
        private val binding: ServicesBinding,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.servicename3.text = product.title
            Glide.with(binding.serviceImage)
                .load(product.imageUrl)
                .placeholder(R.drawable.designer_2)
                .error(R.drawable.designer_2)
                .into(binding.serviceImage)
            binding.root.setOnClickListener { onItemClick(product) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product): Boolean = old == new
        }
    }
}
