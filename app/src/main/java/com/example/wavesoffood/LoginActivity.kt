package com.example.wavesoffood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood.databinding.ActivityLoginBinding
import com.example.wavesoffood.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private  var username: String?=null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var  googleSignInClient: GoogleSignInClient
    private val binding : ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()


        //initializtion of firebase auth
        auth = Firebase.auth

        //initialization of database
        database = Firebase.database.reference

        // initialize google sign in
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

      //login with email aur password
        binding.loginButton.setOnClickListener {
            // get data from text field
            email =binding.emailAddress.text.toString().trim()
            password = binding.Password.text.toString().trim()

            if (email.isEmpty()||password.isBlank()) {
                Toast.makeText(this, "Please fill all details😏", Toast.LENGTH_SHORT).show()
            }else{
                createUser(email,password)
                Toast.makeText(this, " Login Successfull😊", Toast.LENGTH_SHORT).show()
            }

        }
        binding.notaccount.setOnClickListener {
            startActivity(Intent(this, SignActivity::class.java))
            finish()
        }
        //google sign-in
        binding.googleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(this, "sign-in  Successfull😊", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "sign-in failed😔", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "sign-in failed😔", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private fun createUser(email: String, password: String) {
       auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
           task ->
           if (task.isSuccessful){
               val user =auth.currentUser
               Toast.makeText(this, "Login Successfull😊", Toast.LENGTH_SHORT).show()
               updateUi(user)
           }else {
               auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                       task ->
                   if (task.isSuccessful){
                       val user =auth.currentUser
                       saveUserData()
                       updateUi(user)
                   }else{
                       Toast.makeText(this, "sign-in failed😔", Toast.LENGTH_SHORT).show()
                   }
               }

           }
       }
    }

    private fun saveUserData() {
        email =binding.emailAddress.text.toString().trim()
        password = binding.Password.text.toString().trim()
        val user = UserModel(  username,email, password)
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid

        // save data in to database
        database.child("user").child(userId).setValue(user)

    }
    override fun onStart() {
        super.onStart()
        val currentUser= auth.currentUser
        if (currentUser!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}