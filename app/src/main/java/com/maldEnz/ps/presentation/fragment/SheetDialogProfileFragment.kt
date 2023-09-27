package com.maldEnz.ps.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maldEnz.ps.databinding.FragmentSheetDialogProfileBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class SheetDialogProfileFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSheetDialogProfileBinding
    private lateinit var userViewModel: UserViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        binding.btnSave.setOnClickListener {
            updateName()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSheetDialogProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateName() {
        val activity = requireActivity()
        val newName = binding.name.text.toString()
        if (newName.isNotEmpty() || newName != "") {
            userViewModel.updateProfileName(activity, newName)
            dismiss()
        } else {
            Toast.makeText(activity, "Empty Name", Toast.LENGTH_SHORT).show()
        }
    }
}
