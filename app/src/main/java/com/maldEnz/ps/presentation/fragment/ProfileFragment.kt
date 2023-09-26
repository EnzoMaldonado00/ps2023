package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private var profileImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View { // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser!!.uid
        getProfilePicture(currentUser)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater)
    }

    private fun getProfilePicture(currentUser: String) {
        val documentReference =
            FirebaseFirestore.getInstance().collection("Users").document(currentUser)

        documentReference.get().addOnSuccessListener {
            if (it.exists()) {
                val imageUrl = it.getString("image")
                if (imageUrl != null) {
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.profilePictureFragment)
                } else {
                    // handle error
                }
            }
        }
    }
}
