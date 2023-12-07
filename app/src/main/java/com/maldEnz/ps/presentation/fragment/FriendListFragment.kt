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
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class FriendListFragment : Fragment() {

    private lateinit var binding: FragmentFriendListBinding
    private lateinit var adapter: FriendListAdapter
    private val userViewModel: UserViewModel by inject()
    private val friendViewModel: FriendViewModel by inject()

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
        userViewModel.loadUserFriends()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = FriendListAdapter(friendViewModel)
        binding.recycler.adapter = adapter
        binding.emptyStateFriendList.visibility = View.VISIBLE
        userViewModel.friends.observe(viewLifecycleOwner) {
            binding.emptyStateFriendList.visibility = View.GONE
            adapter.submitList(it)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HomeActivity).enableComponents()
    }
}
