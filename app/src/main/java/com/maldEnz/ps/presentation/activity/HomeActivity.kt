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
import com.maldEnz.ps.presentation.fragment.FeedFragment
import com.maldEnz.ps.presentation.fragment.FriendListFragment
import com.maldEnz.ps.presentation.fragment.FriendRequestFragment
import com.maldEnz.ps.presentation.fragment.RecentChatsFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
        FunUtils.setAppTheme(this)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userViewModel.setUserToken()
        userViewModel.imageURL.observe(this) {
            Glide.with(this)
                .load(it)
                .into(binding.profilePicture)
        }

        userViewModel.coins.observe(this) {
            binding.coins.text = it.toString()
        }

        profileOptions()

        binding.profilePicture.setOnClickListener {
            binding.placeholder.performClick()
        }
        binding.btnFab.setOnClickListener {
            deployFrag(AddFriendFragment())
        }
        bottomNavMenu()

        binding.coinContainer.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }
    }

    private fun profileOptions() {
        binding.placeholder.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            val adminItem = popupMenu.menu.findItem(R.id.admin_item)

            userViewModel.isAdmin.observe(this) { admin ->
                adminItem.isVisible = admin
            }

            popupMenu.setForceShowIcon(true)
            popupMenu.gravity = Gravity.END

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.profile_item -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }

                    R.id.friend_request_item -> {
                        deployFragHome(FriendRequestFragment())
                        true
                    }

                    R.id.settings_item -> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        true
                    }

                    R.id.admin_item -> {
                        startActivity(Intent(this, AdminActivity::class.java))
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
        } else {
            userViewModel.getUserData()
            userViewModel.updateUserStatusToOnline()
            deployFragHome(RecentChatsFragment())
        }
    }

    private fun deployFragHome(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(binding.frameContainer.id, fragment)

        transaction.commit()
    }

    private fun deployFrag(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager

        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(binding.frameContainer.id, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun bottomNavMenu() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_home -> {
                    deployFragHome(RecentChatsFragment())
                    true
                }

                R.id.bottom_nav_feed -> {
                    deployFrag(FeedFragment())
                    true
                }

                R.id.bottom_nav_post -> {
                    startActivity(Intent(this, UploadPostActivity::class.java))
                    true
                }

                R.id.bottom_nav_game -> {
                    startActivity(Intent(this, GameActivity::class.java))
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
            btnFab.isEnabled = true
            btnFab.visibility = View.VISIBLE
        }
    }

    fun disableComponents() {
        binding.apply {
            placeholder.isEnabled = false
            btnFab.isEnabled = false
            btnFab.visibility = View.GONE
        }
    }
}
