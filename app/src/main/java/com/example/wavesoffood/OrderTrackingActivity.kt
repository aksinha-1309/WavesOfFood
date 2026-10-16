package com.example.wavesoffood // Apne project ke package ka naam yahan rakhein

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.wavesoffood.databinding.ActivityOrderTrackingBinding // Apne binding ka import check kar lein
import com.google.firebase.auth.FirebaseAuth

class OrderTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderTrackingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Database se live status fetch karna shuru karein
        getStatusUpdates()

        // Back button ya close button ka code agar ho toh yahan likh sakte hain
//        binding.b.setOnClickListener {
//            finish()
//        }
    }

    private fun getStatusUpdates() {
        // हम एक साथ तीनों संभव की (Keys) चेक कर रहे हैं ताकि कहीं से भी ID आए, खाली न रहे
        val orderId = intent.getStringExtra("OrderId")
            ?: intent.getStringExtra("itemPushKey")
            ?: intent.getStringExtra("orderId")

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (orderId.isNullOrEmpty()) {
            Toast.makeText(this, "Order ID nahi mili!", Toast.LENGTH_SHORT).show()
            return
        }

        val databaseRef = FirebaseDatabase.getInstance().reference

        // 1. सबसे पहले 'OrderDetails' (Live Orders) में चेक करें
        databaseRef.child("OrderDetails").child(orderId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val orderAccepted = snapshot.child("orderAccepted").getValue(Boolean::class.java) ?: false
                    val outForDelivery = snapshot.child("outForDelivery").getValue(Boolean::class.java) ?: false
                    val itemReceived = snapshot.child("itemReceived").getValue(Boolean::class.java) ?: false

                    updateTrackingSteps(orderAccepted, outForDelivery, itemReceived)
                } else {
                    // 2. लाइव में न मिलने पर सीधा यूजर की 'BuyHistory' से डेटा उठाएं
                    if (userId.isNotEmpty()) {
                        databaseRef.child("user").child(userId).child("BuyHistory").child(orderId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(historySnapshot: DataSnapshot) {
                                    if (historySnapshot.exists()) {
                                        val orderStatus = historySnapshot.child("orderStatus").getValue(String::class.java) ?: ""

                                        // अगर ऑर्डर डिलीवर हो चुका है या हिस्ट्री में है, तो ट्रैकिंग ग्रीन कर दें
                                        updateTrackingSteps(true, true, true)
                                    } else {
                                        Toast.makeText(this@OrderTrackingActivity, "Order data nahi mila", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@OrderTrackingActivity, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OrderTrackingActivity, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateTrackingSteps(orderAccepted: Boolean, outForDelivery: Boolean, itemReceived: Boolean) {
        // Step 2: Order Accepted ka status check karke color badlein
        if (orderAccepted) {
            binding.ivStep2.setColorFilter(Color.parseColor("#4CAF50"))
            binding.line2.setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        // Step 3: Out For Delivery ka status check karke color badlein
        if (outForDelivery) {
            binding.ivStep3.setColorFilter(Color.parseColor("#4CAF50"))
            binding.line3.setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        // Step 4: Item Received / Delivered ka status check karke color badlein
        if (itemReceived) {
            binding.ivStep4.setColorFilter(Color.parseColor("#4CAF50"))
        }
    }
}