package com.example.electro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.electro.databinding.ServicesBinding

class ServiceAdapter(private val serviceName_3: List<String>, private val serviceImaage_2: List<Int>):
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {
    inner class ServiceViewHolder(private val binding:ServicesBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                servicename3.text=serviceName_3[position]
                serviceImage.setImageResource(serviceImaage_2[position])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceAdapter.ServiceViewHolder{
        val binding= ServicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceAdapter.ServiceViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
       return serviceName_3.size
    }

}