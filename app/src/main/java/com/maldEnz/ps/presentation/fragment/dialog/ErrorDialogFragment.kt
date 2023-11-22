package com.maldEnz.ps.presentation.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.maldEnz.ps.databinding.FragmentErrorDialogBinding

class ErrorDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentErrorDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentErrorDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        private const val ARGS_MESSAGE_KEY = "message"

        @JvmStatic
        fun newInstance(message: String): ErrorDialogFragment {
            val fragment = ErrorDialogFragment()
            val args = Bundle()
            args.putString(ARGS_MESSAGE_KEY, message)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val errorMessage = arguments?.getString(ARGS_MESSAGE_KEY)
        binding.errorMsg.text = errorMessage
        binding.btnAccept.setOnClickListener {
            dismiss()
        }
    }
}
