package com.example.tiktok_analog.ui.menuscreens.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tiktok_analog.R
import com.example.tiktok_analog.databinding.ItemNotificationBinding
import com.example.tiktok_analog.databinding.FragmentNotificationsBinding
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.util.RequestWorker

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        updateNotifications()

        binding.notificationsSwipeRefresh.setOnRefreshListener {
            binding.notificationsSwipeRefresh.isRefreshing = false

            updateNotifications()

            Toast.makeText(
                requireContext(),
                "Notifications Updated", Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }

    private fun addNotificationView(text: String, type: NotificationType, creationTime: Float) {
        val viewBinding = ItemNotificationBinding.inflate(
            layoutInflater,
            binding.notificationLayout,
            true
        )
        viewBinding.text.text = text
        viewBinding.time.text = creationTime.toString()

        viewBinding.icon.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                when (type) {
                    NotificationType.RateVideo -> R.drawable.ic_profile
                    NotificationType.Subscribe -> R.drawable.ic_favorite
                    NotificationType.Broadcast -> R.drawable.ic_starttranslation
                    NotificationType.RateComment -> R.drawable.ic_comment
                }
            )
        )
    }

    private fun updateNotifications() {
        binding.notificationLayout.removeAllViews()

        RequestWorker.getNotifications(
            (requireActivity() as OpenVideoActivity).userData.userId.toInt()
        ) {
            requireActivity().runOnUiThread {
                binding.textNoNotifications.visibility =
                    if (it.length() == 0) View.VISIBLE
                    else View.GONE

                for (index in 0 until it.length()) {
                    val jsonObject = it.getJSONObject(index)
                    addNotificationView(
                        text = jsonObject.getString("text"),
                        type = when (jsonObject.getInt("notificationType")) {
                            0 -> NotificationType.RateVideo
                            1 -> NotificationType.Subscribe
                            2 -> NotificationType.Broadcast
                            else -> NotificationType.RateComment
                        },
                        creationTime = jsonObject.getDouble("creationTime").toFloat()
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private enum class NotificationType {
    RateVideo,
    Subscribe,
    Broadcast,
    RateComment
}