package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.DetailsActivity
import com.example.wavesoffood.databinding.CartItemBinding
import com.example.wavesoffood.databinding.MenuIttemBinding
import com.example.wavesoffood.model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MenuViewHolder {
        val binding = MenuIttemBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(p0: MenuViewHolder, p1: Int) {
        p0.bind(p1)
    }

    override fun getItemCount(): Int = menuItems.size


    inner class MenuViewHolder(private val binding: MenuIttemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }


            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]

            // a intent to open details activity and pass data
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemPrice", menuItem.foodPrice)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", "foodIngredient")

            }
            // start the details activity
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menufoodname.text = menuItem.foodName
                menuprice.text = menuItem.foodPrice
                if (!menuItem.foodImage.isNullOrEmpty()) {
                    try {
                        val imageBytes = android.util.Base64.decode(
                            menuItem.foodImage,
                            android.util.Base64.DEFAULT
                        )
                        val decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(
                            imageBytes,
                            0,
                            imageBytes.size
                        )
                        menuImage.setImageBitmap(decodedBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}












