package com.maldEnz.ps.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.maldEnz.ps.databinding.ActivitySettingsBinding
import com.maldEnz.ps.presentation.fragment.dialog.PasswordReqFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FunUtils.setAppTheme(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.logOut.setOnClickListener {
            logOut()
        }
        binding.deleteAccount.setOnClickListener {
            PasswordReqFragment().show(supportFragmentManager, "Delete")
        }

        binding.themes.setOnClickListener {
            startActivity(Intent(this, UserThemesActivity::class.java))
        }

        binding.faq.setOnClickListener {
            startActivity(Intent(this, QuestionsActivity::class.java))
        }
    }

    private fun logOut() {
        FirebaseMessaging.getInstance().deleteToken()
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        val sharedPreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("selectedTheme")
        editor.apply()
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
