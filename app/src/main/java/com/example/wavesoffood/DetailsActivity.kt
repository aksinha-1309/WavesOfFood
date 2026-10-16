package com.example.wavesoffood

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.databinding.ActivityDetailsBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodName: String? = null
    private var foodPrice: String? = null
    private var foodDescription: String? = null
    private var foodImage: String? = null
    private var foodIngredient: String? = null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // initialize Firebase auth
        auth = FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding) {
            detailsfoodname.text = foodName
            detailsDescription.text = foodDescription
            detailIngredients.text = foodIngredient
         // image ko decode kr rha hai firebase database se
            if (!foodImage.isNullOrEmpty()) {
                try {
                    val imageBytes = android.util.Base64.decode(
                        foodImage,
                        android.util.Base64.DEFAULT
                    )
                    val decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(
                        imageBytes,
                        0,
                        imageBytes.size
                    )
                    detailsfoodImage.setImageBitmap(decodedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.imageButton2.setOnClickListener {
                finish()
            }
            binding.additemButton.setOnClickListener {
                addItemToCart()

            }
        }
    }

    private fun addItemToCart() {
       val database = FirebaseDatabase.getInstance().reference
        // currentUser aa jayega ise
        val userId = auth.currentUser?.uid?:""

        // create a cartitem object
        val cartItem = CartItems(foodName.toString(),foodPrice.toString(),foodDescription.toString(),foodImage.toString(),1)

        // save data to cart item to firebase
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "Items Added into successfully😊", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Item not added", Toast.LENGTH_SHORT).show()
        }
    }
}
