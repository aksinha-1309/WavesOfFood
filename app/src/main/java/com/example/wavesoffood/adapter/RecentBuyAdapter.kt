package com.example.wavesoffood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.wavesoffood.databinding.RecentBuyItemBinding

class RecentBuyAdapter(
    private var context: Context,
    private var foodNameList: ArrayList<String>,
    private var foodImageList: ArrayList<String>,
    private var foodPriceList: ArrayList<String>,
    private var foodNQuantityList: ArrayList<Int>,
) : RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecentViewHolder {
        val binding = RecentBuyItemBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(p0: RecentViewHolder, p1: Int) {
        p0.bind(p1)
    }

    override fun getItemCount(): Int = foodNameList.size


    inner class RecentViewHolder(private val binding: RecentBuyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(p1: Int) {
            val uriString =foodImageList[p1]
            binding.apply {
                foodName.text = foodNameList[p1]
                foodPrice.text = foodPriceList[p1]
              //  foodImage.text = foodNameList[p1]
                foodQuantity.text = foodNQuantityList[p1].toString()
                if (!uriString.isNullOrEmpty()) {
                    try {
                        val imageBytes = android.util.Base64.decode(
                            uriString,
                            android.util.Base64.DEFAULT
                        )
                        val decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(
                            imageBytes,
                            0,
                            imageBytes.size
                        )
                        foodImage.setImageBitmap(decodedBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        }

    }
}