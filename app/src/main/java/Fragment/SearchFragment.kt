package Fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.electro.R
import com.example.electro.adapter.ServiceAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electro.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: ServiceAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private val filterServiceName= mutableListOf<String>()
    private val filterServiceImage= mutableListOf<Int>()
    val searchServiceName= listOf(
        "Wiring Service",
        "Fore ceiling Service",
        "Washing Machine Service",
        "AC Service",
        "Fan Service",
        "Refrigerator Service",
        "Chimney Service"
    )
    val searchServiceImage= listOf(
        R.drawable.designer_2,
        R.drawable.designer3,
        R.drawable.designer_5,
        R.drawable.designer_6,
        R.drawable.designer_8,
        R.drawable.designer_9,
        R.drawable.designer_10
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSearchBinding.inflate(inflater,container,false)
        adapter=ServiceAdapter(filterServiceName,filterServiceImage)
        binding.menuRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter=adapter
        setupSearchView()
        //show all items
        showAllServices()

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAllServices() {
        filterServiceName.clear()
        filterServiceImage.clear()
        filterServiceName.addAll(searchServiceName)
        filterServiceImage.addAll(searchServiceImage)
        adapter.notifyDataSetChanged()


    }
    //setup searchview


    private fun setupSearchView() {
        binding.searchView2.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterServices(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterServices(newText)
                return true
            }
        })
    }

    private fun filterServices(query: String?) {
        filterServiceName.clear()
        filterServiceImage.clear()

        searchServiceName.forEachIndexed { index, serviceName ->
            if (serviceName.contains(query.toString(),ignoreCase = true)){
                filterServiceName.add(serviceName)
                filterServiceImage.add(searchServiceImage[index])
            }
        }
        adapter.notifyDataSetChanged()
    }
}