package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maldEnz.ps.databinding.FragmentAddFriendBinding
import com.maldEnz.ps.presentation.activity.HomeActivity
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class AddFriendFragment : Fragment() {

    private lateinit var binding: FragmentAddFriendBinding
    private val userViewModel: UserViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        (activity as HomeActivity).disableComponents()
        return binding.root
    }

    private fun getFriendMail(): String {
        return binding.mail.text.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        binding.btnSendFriendRequest.setOnClickListener {
            userViewModel.sendFriendRequest(getFriendMail(), activity)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HomeActivity).enableComponents()
    }
}
