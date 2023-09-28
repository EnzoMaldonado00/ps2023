package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.FragmentPasswordReqBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class PasswordReqFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPasswordReqBinding
    private val userViewModel: UserViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPasswordReqBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getPasswordInput() {
        userViewModel.passwordAuth.value = binding.password.text.toString()
        dismiss()
    }

    private fun deleteAccount() {
        val activity = requireActivity()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(
                activity,
                "You must be signed in to delete your account.",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        val password = userViewModel.passwordAuth.value.toString()

        val cred = EmailAuthProvider.getCredential(user.email!!, password)

        user.reauthenticate(cred).addOnCompleteListener {
            if (it.isSuccessful) {
                user.delete().addOnCompleteListener { delete ->
                    if (delete.isSuccessful) {
                        activity.finish()
                    } else {
                        Toast.makeText(
                            activity,
                            "Failed to delete account.",
                            Toast.LENGTH_SHORT,
                        )
                            .show()
                    }
                }
            } else {
                Toast.makeText(activity, "The password is incorrect.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun buttonClick() {
        binding.btnOk.setOnClickListener {
            if (binding.password.text.toString()
                    .isNotEmpty() || binding.password.text.toString() != ""
            ) {
                getPasswordInput()
                deleteAccount()
            } else {
                Toast.makeText(activity, "The password is empty.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}
