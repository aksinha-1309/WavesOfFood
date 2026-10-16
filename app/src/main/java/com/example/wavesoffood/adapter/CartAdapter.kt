package com.example.wavesoffood.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrice: MutableList<String>,
    private var cartDescription: MutableList<String>,
    private var cartImage: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
    private val cartIngredient: MutableList<String>

) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    //instance firebase
    private val auth = FirebaseAuth.getInstance()

    init {
        //initilaze firebase
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItems.size

        itemQuentities = IntArray(cartItemNumber){1}
        cartItemsRefrence =database.reference.child("user").child(userId).child("CartItems")

    }
    companion object{
        private var itemQuentities: IntArray=intArrayOf()
        private lateinit var cartItemsRefrence: DatabaseReference
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(p0: CartViewHolder, p1: Int) {

        p0.bind(p1)
    }

    override fun getItemCount(): Int = cartItems.size
    // get update quantity
    fun getUpdatedItemsQuantities(): MutableList<Int> {
       val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity

    }


    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(p1: Int) {
            val uriString =cartImage[p1]
           // Log.d("image", "bind: $uriString") logcat me error find krne me usehota hai
            binding.apply {
                val qunatity = itemQuentities[p1]
                cartfoodName.text = cartItems[p1]
                cartfoodPrice.text = cartItemPrice[p1]

                cartQunatity.text = qunatity.toString()
                // image ko decode kr rha hai firebase database se

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
                        cartfoodImage.setImageBitmap(decodedBitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                minusButton.setOnClickListener {
                    decreaseQuantity(p1)
                }
                plusButton.setOnClickListener {
                    increaseQuantity(p1)
                }
                deleteButton.setOnClickListener {
                    val itemPosition = absoluteAdapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }


        private fun decreaseQuantity(p1: Int) {
            if (itemQuentities[p1] > 1) {
                itemQuentities[p1]--
                cartQuantity[p1] = itemQuentities[p1]
                binding.cartQunatity.text = itemQuentities[p1].toString()
            }
        }


        private fun increaseQuantity(p1: Int) {
            if (itemQuentities[p1] < 10) {
                itemQuentities[p1]++
                cartQuantity[p1] = itemQuentities[p1]
                binding.cartQunatity.text = itemQuentities[p1].toString()
            }
        }

        private fun deleteItem(p1: Int) {
           val positionRetrieve =p1
            getUniqueKeyAtPosition(positionRetrieve){uniqueKey ->
                if (uniqueKey!=null){
                    removeItem(p1,uniqueKey)
                }
            }

        }

        private fun removeItem(p1: Int, uniqueKey: String) {
            if (uniqueKey!=null){
               cartItemsRefrence.child(uniqueKey).removeValue().addOnSuccessListener {
                   cartItems.removeAt(p1)
                   cartImage.removeAt(p1)
                   cartDescription.removeAt(p1)
                   cartQuantity.removeAt(p1)
                   cartItemPrice.removeAt(p1)
                   cartIngredient.removeAt(p1)
                   Toast.makeText(context, "Item delete", Toast.LENGTH_SHORT).show()
                   // update item quantities
                   itemQuentities =itemQuentities.filterIndexed { index, i -> index!=p1}.toIntArray()
                   notifyItemRemoved(p1)
                   notifyItemRangeChanged(p1,cartItems.size)
               } .addOnFailureListener {
                   Toast.makeText(context, "failed to delete", Toast.LENGTH_SHORT).show()
               }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int,onComplete:(String?)-> Unit) {
            cartItemsRefrence.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                  var uniqueKey: String?=null
                  //loop for snapshot children
                  p0.children.forEachIndexed { index, snapshot ->
                      if (index==positionRetrieve){
                          uniqueKey = snapshot.key
                          return@forEachIndexed
                      }

                  }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
    }
}





