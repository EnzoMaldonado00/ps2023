package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ActivityHomeBinding
import com.maldEnz.ps.presentation.fragment.AddFriendFragment
import com.maldEnz.ps.presentation.fragment.ChatsFragment
import com.maldEnz.ps.presentation.fragment.FriendListFragment
import com.maldEnz.ps.presentation.fragment.FriendRequestFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.ChatViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by inject()
    private val chatViewModel: ChatViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel.getUserData()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        userViewModel.imageURL.observe(this) {
            Glide.with(this)
                .load(String.format("%s", it))
                .into(binding.profilePicture)
        }
        deployFrag(ChatsFragment())
        profileOptions()

        binding.profilePicture.setOnClickListener {
            binding.placeholder.performClick()
        }
        binding.btnFab.setOnClickListener {
            deployFrag(AddFriendFragment())
        }
        bottomNavMenu()
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
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }

                    R.id.friend_request_item -> {
                        deployFrag(FriendRequestFragment())
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
        userViewModel.getUserData()
    }

    private fun deployFrag(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager

        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(binding.frameContainer.id, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun bottomNavMenu() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    deployFrag(ChatsFragment())
                    true
                }

                R.id.bottom_nav_friends -> {
                    deployFrag(FriendListFragment())
                    true
                }

                else -> false
            }
        }
    }

    fun enableComponents() {
        binding.apply {
            placeholder.isEnabled = true
            profilePicture.isEnabled = true
            btnFab.isEnabled = true
            bottomNav.isEnabled = true
            bottomNav.visibility = View.VISIBLE
        }
    }

    fun disableComponents() {
        binding.apply {
            placeholder.isEnabled = false
            profilePicture.isEnabled = false
            btnFab.isEnabled = false
            bottomNav.isEnabled = false
        }
    }
}
