package com.example.wavesoffood.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.RecentOrderItems
import com.example.wavesoffood.adapter.BuyAgainAdapter
import com.example.wavesoffood.databinding.FragmentHistoryBinding
import com.example.wavesoffood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val buyAgainOrderIdentifier = mutableListOf<String>()
    private var userId: String = ""
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // यूजर की ऑर्डर हिस्ट्री रिट्रीव करें
        retrieveBuyHistory()

        binding.recentbuyitem.setOnClickListener {
            seeItemsRecentBuy()
        }

        // रिसीव बटन क्लिक करने पर ऑर्डर अपडेट होगा और अगला ऑर्डर ऊपर आ जाएगा
        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        // लिस्ट का सबसे पहला (रीसेंट/ऊपर वाला) ऑर्डर फेच करें
        val recentOrderItem = listOfOrderItem.firstOrNull()
        val itemPushKey = recentOrderItem?.itemPushKey

        if (!itemPushKey.isNullOrEmpty()) {
            // 1. डेटाबेस में 'Completed Order' के अंदर स्टेटस अपडेट करें
            val completeOrderReference =
                database.reference.child("Completed Order").child(itemPushKey)
            completeOrderReference.child("paymentReceived").setValue(true)

            // 2. यूजर की 'BuyHistory' में ऑर्डर का स्टेटस 'Delivered' अपडेट करें
            val userBuyHistoryRef =
                database.reference.child("user").child(userId).child("BuyHistory")
                    .child(itemPushKey)

            val updates = mapOf<String, Any>(
                "paymentReceived" to true,
                "orderStatus" to "Delivered"
            )

            userBuyHistoryRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(context, "Order Marked as Delivered", Toast.LENGTH_SHORT).show()

                // 3. लिस्ट को दोबारा सर्वर से लोड करें ताकि पहला वाला हट जाए और अगला ऑर्डर ऑटोमैटिक स्क्रीन पर आ जाए
                retrieveBuyHistory()

            }.addOnFailureListener {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No order found to update", Toast.LENGTH_SHORT).show()
        }
    }

    private fun seeItemsRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem", listOfOrderItem)
            startActivity(intent)
        }
    }

    private fun retrieveBuyHistory() {
        binding.recentbuyitem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")

        buyItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()
                val previousBuyList =
                    mutableListOf<OrderDetails>() // नीचे वाले इतिहास के लिए अलग लिस्ट

                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        // अगर ऑर्डर डिलीवर हो चुका है, तो उसे प्रीवियस हिस्ट्री लिस्ट में डालें
                        if (it.paymentReceived == true) {
                            previousBuyList.add(it)
                        } else {
                            // जो अभी डिलीवर नहीं हुए हैं, उन्हें रीसेंट/पेंडिंग लिस्ट में रखें
                            listOfOrderItem.add(it)
                        }
                    }
                }

                // 1. रीसेंट ऑर्डर्स को सॉर्ट करें और चेक करें कि कौन सा एडमिन द्वारा एक्सेप्टेड है
                listOfOrderItem.sortByDescending { it.currentTime }
                val acceptedOrder = listOfOrderItem.find { __it -> __it.orderAccepted == true }

                if (acceptedOrder != null) {
                    listOfOrderItem.remove(acceptedOrder)
                    listOfOrderItem.add(0, acceptedOrder)
                    setDataInRecentBuyItem()
                    binding.recentbuyitem.visibility = View.VISIBLE
                } else if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                    binding.recentbuyitem.visibility = View.VISIBLE
                } else {
                    binding.recentbuyitem.visibility = View.GONE
                }

                // 2. नीचे वाले 'Previously buy' सेक्शन के लिए प्रीवियस लिस्ट सेट करें
                setPreviousBuyItemsRecyclerView(previousBuyList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load history", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setDataInRecentBuyItem() {
        binding.recentbuyitem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let { order ->
            with(binding) {
                buyagainfoodname.text = order.foodNames?.firstOrNull() ?: ""
                buyagainfoodprice.text = order.foodPrices?.firstOrNull() ?: ""
                val image = order.foodImages?.firstOrNull() ?: ""
                if (!image.isNullOrEmpty()) {
                    try {
                        val imageBytes = android.util.Base64.decode(
                            image,
                            android.util.Base64.DEFAULT
                        )
                        val decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(
                            imageBytes,
                            0,
                            imageBytes.size
                        )
                        buyagainfoodimage.setImageBitmap(decodedBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val isOrderAccepted = order.orderAccepted
                if (isOrderAccepted) {
                    orderstutus.background.setTint(android.graphics.Color.GREEN)
                    receivedButton.visibility = View.VISIBLE
                } else {
                    receivedButton.visibility = View.GONE
                }
            }
        }
    }

    private fun setPreviousBuyItemsRecyclerView(previousList: List<OrderDetails>) {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()
        buyAgainOrderIdentifier.clear()
        // डिलीवर्ड ऑर्डर्स की लिस्ट से डेटा निकालकर अलग लिस्ट में भरें
        for (order in previousList) {
            order.foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            order.foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
            order.foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
            order.itemPushKey?.let { buyAgainOrderIdentifier.add(it) }
        }

        val rv = binding.buyagainrecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())

        val buyAgainAdapter = BuyAgainAdapter(
            buyAgainFoodName,
            buyAgainFoodPrice,
            buyAgainFoodImage,
            requireContext(),
            buyAgainOrderIdentifier,
            userId,


            )
        rv.adapter = buyAgainAdapter
    }

}
