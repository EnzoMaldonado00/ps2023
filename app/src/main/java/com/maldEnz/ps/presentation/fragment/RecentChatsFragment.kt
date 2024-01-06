package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.FragmentChatsBinding
import com.maldEnz.ps.presentation.adapter.RecentChatsAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.ChatViewModel
import org.koin.android.ext.android.inject

class RecentChatsFragment : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private lateinit var adapter: RecentChatsAdapter

    private val chatViewModel: ChatViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecentChatsAdapter()
        binding.chatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatsRecyclerView.adapter = adapter
        binding.chatsRecyclerView.itemAnimator = null
        chatViewModel.loadRecentChats()
        binding.progressBar.visibility = View.VISIBLE
        chatViewModel.chatList.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                binding.emptyStateChats.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                adapter.submitList(list)
            } else {
                binding.emptyStateChats.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
