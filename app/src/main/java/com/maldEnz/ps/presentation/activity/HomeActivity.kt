package com.maldEnz.ps.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ActivityHomeBinding
import com.maldEnz.ps.presentation.fragment.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val idCurrentUser = currentUser!!.uid
        getProfilePicture(idCurrentUser)
        profileOptions()
        onBackHandler()

        binding.profilePicture.setOnClickListener {
            binding.placeholder.performClick()
        }
    }

    private fun getProfilePicture(currentUser: String) {
        val documentReference = firestore.collection("Users").document(currentUser)

        documentReference.get().addOnSuccessListener {
            if (it.exists()) {
                val imageUrl = it.getString("image")
                if (imageUrl != null) {
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.profilePicture)
                } else {
                    // handle error
                }
            }
        }
    }

    private fun profileOptions() {
        binding.placeholder.setOnClickListener {
            val popupMenu = PopupMenu(this, it)

            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setForceShowIcon(true)
            popupMenu.gravity = Gravity.END

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.profile_item -> {
                        deployFrag(ProfileFragment())
                        true
                    }

                    R.id.settings_item -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }

    private fun deployFrag(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        binding.frameContainer.visibility = View.VISIBLE
        binding.placeholder.visibility = View.GONE
        binding.profilePicture.visibility = View.GONE
        binding.chats.visibility
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(binding.frameContainer.id, fragment) // Reemplaza "fragment_container" con el ID de tu contenedor
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun closeFragment(){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = supportFragmentManager.findFragmentById(binding.frameContainer.id)

        if (fragment != null) {
            fragmentTransaction.remove(fragment)
            fragmentTransaction.commit()
        }
    }

    private fun onBackHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.frameContainer.visibility = View.GONE
                closeFragment()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}
