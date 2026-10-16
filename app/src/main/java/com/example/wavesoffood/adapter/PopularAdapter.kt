package com.example.wavesoffood.adapter

import android.R
import android.content.Context
import android.content.Intent
import android.icu.text.Transliterator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.KeyPosition
import androidx.core.view.RoundedCornerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.DetailsActivity
import com.example.wavesoffood.databinding.FragmentHomeBinding
import com.example.wavesoffood.databinding.PopularItemBinding

class PopularAdapter(private val items : List<String>,private val price : List<String>, private val image : List<Int>,private val requireContext : Context): RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    override fun onCreateViewHolder(
        p0: ViewGroup,
        viewType: Int
    ): PopularViewHolder {
       return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(p0.context),p0,false))
    }

    override fun onBindViewHolder(p0: PopularViewHolder, position: Int) {
       val item = items[position]
        val images = image[position]
        val price = price[position]
        p0.bind(item,price,images)

        p0.itemView.setOnClickListener {
            val intent = Intent(requireContext, DetailsActivity::class.java)
            intent.putExtra("MenuItemName", item)
            intent.putExtra("MenuItemImage", images)
            requireContext.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PopularViewHolder (private val binding: PopularItemBinding): RecyclerView.ViewHolder(binding.root){
      private val imagesView = binding.imageView5
        fun bind(item: String,price : String, images: Int) {
            binding.foodnamepopular.text = item
            binding.pricepopular.text = price
            imagesView.setImageResource(images)
        }

    }
}