package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.OrderTrackingActivity
import com.example.wavesoffood.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val buyAgainFoodName: MutableList<String>,
    private val buyAgainFoodPrice: MutableList<String>,
    private val buyAgainFoodImage: MutableList<String>,
    private var requireContext: Context,
    private val buyAgainOrderIdentifier: MutableList<String>,
    private val userId: String

) :
    RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(p0: BuyAgainViewHolder, p1: Int) {
        p0.bind(buyAgainFoodName[p1], buyAgainFoodPrice[p1], buyAgainFoodImage[p1])
    }

    override fun getItemCount(): Int = buyAgainFoodName.size
    inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                // यहाँ क्लिक लिसनर जोड़ें ताकि यूजर क्लिक करके ट्रैकिंग पेज पर जा सके
                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // अपनी जरूरत के अनुसार currentUserId और orderId पास करें
                        val intent = Intent(requireContext, OrderTrackingActivity::class.java)
                        intent.putExtra("UserId", userId)
                         intent.putExtra("orderId", buyAgainOrderIdentifier[position])
                        requireContext.startActivity(intent)
                    }
                }
            }



        fun bind(foodName: String, foodPrice: String, foodImage: String) {
            binding.buyagainfoodname.text = foodName
            binding.buyagainfoodprice.text = foodPrice

            val image =foodImage
            if (!image.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(
                        image,
                        Base64.DEFAULT
                    )
                    val decodedBitmap = BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size
                    )
                    binding.buyagainfoodimage.setImageBitmap(decodedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}