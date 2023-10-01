package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.FragmentFriendRequestBinding
import com.maldEnz.ps.presentation.adapter.FriendRequestAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class FriendRequestFragment : Fragment() {

    private lateinit var binding: FragmentFriendRequestBinding
    private lateinit var adapter: FriendRequestAdapter
    private val userViewModel: UserViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.getUserData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        adapter = FriendRequestAdapter(activity)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        userViewModel.friendRequest.observe(viewLifecycleOwner) { friendList ->
            adapter.submitList(friendList)
        }
    }
}
