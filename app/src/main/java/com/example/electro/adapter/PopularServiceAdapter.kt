package com.example.electro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electro.databinding.PopularServiceBinding

class PopularServiceAdapter(private val service:List<String>,private val image:List<Int>): RecyclerView.Adapter<PopularServiceAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularServiceBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
      val services= service[position]
        val images= image[position]
        holder.bind(services,images)
    }
    override fun getItemCount(): Int {
        return service.size
    }

    class PopularViewHolder(private val binding: PopularServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imagesView= binding.imageView5
        fun bind(services: String, images: Int) {
            binding.serviceName.text= services
            imagesView.setImageResource(images)

        }

    }

}