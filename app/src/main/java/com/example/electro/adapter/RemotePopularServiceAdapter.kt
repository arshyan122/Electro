package com.example.electro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.electro.R
import com.example.electro.data.model.Product
import com.example.electro.databinding.PopularServiceBinding

/**
 * Renders remote `Product` items into the existing `popular_service.xml` row
 * layout. Uses ListAdapter + DiffUtil for efficient updates and Glide for
 * image loading from URLs.
 */
class RemotePopularServiceAdapter(
    private val onItemClick: (Product) -> Unit = {}
) : ListAdapter<Product, RemotePopularServiceAdapter.ProductViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = PopularServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        private val binding: PopularServiceBinding,
        private val onItemClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.serviceName.text = product.title
            Glide.with(binding.imageView5)
                .load(product.imageUrl)
                .placeholder(R.drawable.designer_2)
                .error(R.drawable.designer_2)
                .into(binding.imageView5)
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
