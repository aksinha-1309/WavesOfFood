package com.example.wavesoffood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.wavesoffood.MenuBootomSheetFragment
import com.example.wavesoffood.R
import com.example.wavesoffood.adapter.MenuAdapter
import com.example.wavesoffood.databinding.FragmentHomeBinding
import com.example.wavesoffood.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.menuViewButton.setOnClickListener {
            val bottomSheetDialog = MenuBootomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        // बाइंडिंग के जरिए SwipeRefreshLayout को कॉल करें
        binding.swipeRefreshLayout.setOnRefreshListener {
            // यहाँ अपना वो डेटा फेच करने वाला फंक्शन या कोड लिखें
            // जिससे होम स्क्रीन का डेटा (जैसे बैनर, पॉपुलर फूड आइटम्स, और नोटिफिकेशन स्टेटस) दोबारा लोड होता है।

            reloadHomeData()

            // डेटा लोड होने के बाद रिफ्रेशिंग एनीमेशन को बंद करने के लिए:
            binding.swipeRefreshLayout.isRefreshing = false
        }
        // retrieve and display popular item
        retraieveAndDsplayPopularItems()
        return binding.root
    }

    private fun reloadHomeData() {
        retraieveAndDsplayPopularItems()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun retraieveAndDsplayPopularItems() {
        // get refrence to databse
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        //retrieve menu items  from the database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (foodSnapshot in snapshot.children) {
                        val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                        menuItem?.let {
                            menuItems.add(it)
                        }
                    }
                    // display  a random popular items
                    randomPopularItems()
                }


                override fun onCancelled(p0: DatabaseError) {

                }

                private fun randomPopularItems() {
                    // create shuffled list of menu items
                    val index = menuItems.indices.toList().shuffled()
                    val numItemToShow = 6
                    val subsetMenuItem = index.take(numItemToShow).map { menuItems[it] }
                    setPopularItemsAdapter(subsetMenuItem)
                }

                private fun setPopularItemsAdapter(subsetMenuItem: List<MenuItem>) {
                    val adapter = MenuAdapter(subsetMenuItem, requireContext())
                    binding.popularRecyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.popularRecyclerView.adapter = adapter
                }
            })
    }
            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val imageList = ArrayList<SlideModel>()
            imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
            imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
            imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))
            val imageSlider = binding.imageSlider
            imageSlider.setImageList(imageList)
            imageSlider.setImageList(imageList, scaleType = ScaleTypes.FIT)

            imageSlider.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {

                }

                override fun onItemSelected(position: Int) {
                    val itemPosition = imageList[position]
                    val itemMessage = "Selected image $position"
                    Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
                }

            })


           }

}