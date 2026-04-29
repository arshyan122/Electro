package Fragment

import android.content.ClipData.Item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.electro.BottomFragment
import com.example.electro.R
import com.example.electro.adapter.PopularServiceAdapter
import com.example.electro.databinding.FragmentHomeBinding
class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(inflater,container,false)

        binding.allservicebutton.setOnClickListener{
            val bottomSheetDialog=BottomFragment()
            bottomSheetDialog.show(parentFragmentManager,"Test")
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val imageList= ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.designer_2,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.designer3,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.designer_5,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.designer_6,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.designer_9,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.designer_10,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.designer_8,ScaleTypes.FIT))
        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, scaleType = ScaleTypes.FIT)
        imageSlider.setItemClickListener(object :ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(),itemMessage,Toast.LENGTH_SHORT).show()
            }
        })
        val serviceName= listOf("Wiring Service","Fore Ceiling Service","Washing Machine Service")
        val imagesView= listOf(R.drawable.designer_2,R.drawable.designer3,R.drawable.designer_5)
        val adapter= PopularServiceAdapter(serviceName,imagesView)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter= adapter

    }
}