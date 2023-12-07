package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ActivityLogInBinding
import com.maldEnz.ps.presentation.fragment.dialog.ErrorDialogFragment

class LogInActivity : AppCompatActivity() {

    // HANDLE POSSIBLE EXCEPTIONS

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        signInAction()
    }

    private fun signInAction() {
        binding.btnLogin.setOnClickListener {
            if (binding.email.text.isNotEmpty() && binding.password.text.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnProgBar.visibility = View.VISIBLE
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.email.text.toString(),
                    binding.password.text.toString(),
                ).addOnCompleteListener {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.btnProgBar.visibility = View.GONE
                    if (it.isSuccessful) {
                        finish()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.btnProgBar.visibility = View.GONE
                    val dialogFragment =
                        ErrorDialogFragment.newInstance(getString(R.string.error_dialog))
                    dialogFragment.show(supportFragmentManager, "dialog")
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
