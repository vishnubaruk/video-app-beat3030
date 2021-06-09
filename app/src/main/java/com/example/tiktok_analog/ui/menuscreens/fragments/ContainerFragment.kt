package com.example.tiktok_analog.ui.menuscreens.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tiktok_analog.R
import com.example.tiktok_analog.databinding.AddVideoBinding
import com.example.tiktok_analog.databinding.ContainerFragmentBinding

class ContainerFragment() : Fragment(R.layout.container_fragment) {
    private var _binding: ContainerFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContainerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}