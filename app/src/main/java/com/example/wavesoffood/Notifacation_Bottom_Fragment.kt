package com.example.wavesoffood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.NotificationAdapter
import com.example.wavesoffood.databinding.FragmentNotifacationBottomBinding
import com.example.wavesoffood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Notifacation_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotifacationBottomBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    private val notificationList = ArrayList<String>()
    private val imageList = ArrayList<Int>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotifacationBottomBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // एडैप्टर को इनिशियलाइज करें
        adapter = NotificationAdapter(notificationList, imageList)
        binding.notificationrecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationrecyclerView.adapter = adapter

        // डेटाबेस से लाइव स्टेटस फेच करें
        fetchNotificationData()

        return binding.root
    }

    private fun fetchNotificationData() {
        userId = auth.currentUser?.uid ?: ""
        val historyRef = database.reference.child("user").child(userId).child("BuyHistory")

        historyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                imageList.clear()

                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(OrderDetails::class.java)
                    order?.let {
                        if (it.orderAccepted == true) {
                            notificationList.add("Order has been taken by the driver")
                            imageList.add(R.drawable.truck)
                        }
                        if (it.paymentReceived == true) {
                            notificationList.add("Congrats Your Order Placed / Delivered")
                            imageList.add(R.drawable.right)
                        }
                    }
                }

                // अगर कोई डेटा न मिले तो डिफ़ॉल्ट दिखाएं
                if (notificationList.isEmpty()) {
                    notificationList.add("Congrats Your Order Placed")
                    imageList.add(R.drawable.right)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // अगर डेटा लोड होने में कोई दिक्कत आए
            }
        })
    }
}