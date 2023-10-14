package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maldEnz.ps.databinding.FragmentPostsBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class PostsFragment : Fragment() {

    private lateinit var binding: FragmentPostsBinding
    private val userViewModel: UserViewModel by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
