package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.FragmentFriendListBinding
import com.maldEnz.ps.presentation.activity.HomeActivity
import com.maldEnz.ps.presentation.adapter.FriendListAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class FriendListFragment : Fragment() {

    private lateinit var binding: FragmentFriendListBinding
    private lateinit var adapter: FriendListAdapter
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
        binding = FragmentFriendListBinding.inflate(inflater, container, false)
        (activity as HomeActivity).disableComponents()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        adapter = FriendListAdapter(activity)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        userViewModel.friends.observe(viewLifecycleOwner) { friendList ->
            adapter.submitList(friendList)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HomeActivity).enableComponents()
    }
}
