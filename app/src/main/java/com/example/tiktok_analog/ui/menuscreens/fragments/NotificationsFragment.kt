package com.example.tiktok_analog.ui.menuscreens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tiktok_analog.R
import com.example.tiktok_analog.databinding.NotificationsBinding

class NotificationsFragment : Fragment(R.layout.notifications) {
    private var _binding: NotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationsBinding.inflate(inflater, container, false)

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.notificationsSwipeRefresh.setOnRefreshListener {
            binding.notificationsSwipeRefresh.isRefreshing = false
            Toast.makeText(
                requireContext(),
                "Notifications Updated", Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}