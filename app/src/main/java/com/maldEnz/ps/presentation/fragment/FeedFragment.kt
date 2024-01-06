package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.FragmentFeedBinding
import com.maldEnz.ps.presentation.activity.HomeActivity
import com.maldEnz.ps.presentation.adapter.FeedAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.PostViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking
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
        // userViewModel.getFeed()
        runBlocking {
            userViewModel.getFeedAlt(FirebaseAuth.getInstance().currentUser!!.uid)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        adapter = FeedAdapter(postViewModel)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = null
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        binding.progressBar.visibility = View.VISIBLE

        userViewModel.feedPostList.observe(viewLifecycleOwner) { posts ->
            if (posts.isNotEmpty()) {
                val sortedList = posts.sortedBy { post -> post.postModel.timestamp }
                adapter.submitList(sortedList)
                binding.recycler.scrollToPosition(adapter.itemCount - 1)

                binding.progressBar.visibility = View.GONE
                binding.emptyStateFeeds.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.emptyStateFeeds.visibility = View.VISIBLE
            }
        }

        binding.recycler.scrollToPosition(adapter.itemCount - 1)

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && binding.btnJumpTop.visibility != View.VISIBLE) {
                    binding.btnJumpTop.visibility = View.VISIBLE
                }

                val lastItem = layoutManager.findLastVisibleItemPosition()

                if (lastItem != adapter.itemCount - 1) {
                    binding.btnJumpTop.visibility = View.VISIBLE
                } else {
                    binding.btnJumpTop.visibility = View.GONE
                }
            }
        })

        binding.btnJumpTop.setOnClickListener {
            binding.recycler.smoothScrollToPosition(adapter.itemCount)
            binding.btnJumpTop.visibility = View.GONE
        }

        binding.swipe.setOnRefreshListener {
            // userViewModel.getFeed()
            runBlocking {
                userViewModel.getFeedAlt(FirebaseAuth.getInstance().currentUser!!.uid)
            }
            binding.swipe.isRefreshing = false
            binding.btnJumpTop.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as HomeActivity).enableComponents()
    }
}
