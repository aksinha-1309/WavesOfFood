package com.example.wavesoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wavesoffood.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val navController = findNavController(R.id.fragmentContainerView)
        val bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomnav.setupWithNavController(navController)

        // Notification Button Click Listener
        binding.notificationButton.setOnClickListener {
            // Click hote hi red dot gayab ho jaye
            binding.redDot.visibility = View.GONE

            val bottomSheet = Notifacation_Bottom_Fragment()
            bottomSheet.show(supportFragmentManager, "Test")
        }

        // Database se check karein ki naya update hai ya nahi taaki red dot dikhe
        checkNewNotifications()
    }

    private fun checkNewNotifications() {
        val userId = auth.currentUser?.uid ?: return
        val historyRef = database.reference.child("user").child(userId).child("BuyHistory")

        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Agar order history/update maujud hai toh red dot show karein
                    binding.redDot.visibility = View.VISIBLE
                } else {
                    binding.redDot.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error handling agar zaroorat ho
            }
        })
    }
}