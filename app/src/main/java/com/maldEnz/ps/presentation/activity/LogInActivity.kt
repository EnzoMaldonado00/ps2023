package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.maldEnz.ps.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {

    // HANDLE POSSIBLE EXCEPTIONS

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        signInAction()
    }

    private fun signInAction() {
        binding.btnLogin.setOnClickListener {
            if (binding.email.text.isNotEmpty() && binding.password.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.email.text.toString(),
                    binding.password.text.toString(),
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        finish()

                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        // TODO
                        // handle invalid account error
                    }
                }.addOnFailureListener {
                    when (it) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // handle invalid account
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser != null) {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}
