package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.FragmentFriendRequestBinding
import com.maldEnz.ps.presentation.activity.HomeActivity
import com.maldEnz.ps.presentation.adapter.FriendRequestAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class FriendRequestFragment : Fragment() {

    private lateinit var binding: FragmentFriendRequestBinding
    private lateinit var adapter: FriendRequestAdapter
    private val userViewModel: UserViewModel by inject()
    private val friendViewModel: FriendViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        (activity as HomeActivity).disableComponents()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getFriendRequests()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FriendRequestAdapter(friendViewModel)
        binding.recyclerView.adapter = adapter
        binding.emptyStateFriendRequest.visibility = View.VISIBLE
        userViewModel.friendRequest.observe(viewLifecycleOwner) { friendRequests ->
            binding.emptyStateFriendRequest.visibility = View.GONE
            adapter.submitList(friendRequests)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HomeActivity).enableComponents()
    }
}
