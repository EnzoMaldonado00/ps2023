package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivitySettingsBinding
import com.maldEnz.ps.presentation.fragment.dialog.PasswordReqFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.logOut.setOnClickListener {
            logOut()
        }
        binding.deleteAccount.setOnClickListener {
            PasswordReqFragment().show(supportFragmentManager, "Delete")
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        userViewModel.updateUserStatusToOnline()
    }

    override fun onPause() {
        super.onPause()
        userViewModel.updateUserStatusToDisconnected()
    }
}
