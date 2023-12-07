package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.FragmentFeedBinding
import com.maldEnz.ps.presentation.activity.HomeActivity
import com.maldEnz.ps.presentation.adapter.FeedAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.PostViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var adapter: FeedAdapter
    private val userViewModel: UserViewModel by inject()
    private val friendViewModel: FriendViewModel by inject()
    private val postViewModel: PostViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        (activity as HomeActivity).disableComponents()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getFeed()
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = FeedAdapter(postViewModel)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = null
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        binding.emptyStateFeeds.visibility = View.VISIBLE
        userViewModel.feedPostList.observe(viewLifecycleOwner) {
            binding.emptyStateFeeds.visibility = View.GONE
            val sortedList = it.sortedBy { post -> post.postModel.timestamp }
            adapter.submitList(sortedList)
            binding.recycler.scrollToPosition(0)
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HomeActivity).enableComponents()
    }
}
