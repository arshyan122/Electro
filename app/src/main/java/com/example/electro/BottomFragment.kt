package com.example.electro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electro.adapter.ServiceAdapter
import com.example.electro.databinding.FragmentBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding:FragmentBottomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBottomBinding.inflate(inflater,container,false)

        val servicename3= listOf(
            "Wiring Service",
            "Fore ceiling Service",
            "Washing Machine Service",
            "AC Service",
            "Fan Service",
            "Refrigerator Service",
            "Chimney Service"
        )
        val serviceImage= listOf(
            R.drawable.designer_2,
            R.drawable.designer3,
            R.drawable.designer_5,
            R.drawable.designer_6,
            R.drawable.designer_8,
            R.drawable.designer_9,
            R.drawable.designer_10
        )
        val adapter=ServiceAdapter(ArrayList(servicename3),ArrayList(serviceImage))
        binding.menuRecylerView2.layoutManager=LinearLayoutManager(requireContext())
        binding.menuRecylerView2.adapter=adapter
        return binding.root
    }

}