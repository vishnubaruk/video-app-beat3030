package com.example.tiktok_analog.ui.menuscreens.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.databinding.ActivityFavouriteBinding
import com.example.tiktok_analog.databinding.FavVideoItemBinding
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.squareup.picasso.Picasso
import org.json.JSONObject

class FavouriteFragment : Fragment(R.layout.activity_favourite) {
    private lateinit var userData: User

    private var _binding: ActivityFavouriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityFavouriteBinding.inflate(inflater, container, false)

        requireActivity().openFileInput("userData").use {
            userData = User.fromJson(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        fillFavourites()
        binding.favouriteSwipeRefresh.setOnRefreshListener {
            binding.favouriteSwipeRefresh.isRefreshing = false
            Toast.makeText(requireContext(), "Favourite Updated", Toast.LENGTH_SHORT).show()

            fillFavourites()
        }

        return binding.root
    }

    private fun fillFavourites() {
        binding.favouriteLayout.removeAllViews()
        val fillFavouritesQueue = Volley.newRequestQueue(requireContext())

        val url = resources.getString(R.string.base_url) +
                "/getFavourite?" +
                "email=${userData.email}&" +
                "phone=${userData.phone}"

        binding.progressBar.visibility = View.VISIBLE

        val favouriteVideoList = arrayListOf<Int>()
        val getFavouritesRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response).getJSONArray("result")
                for (index in 0 until result.length()) {
                    favouriteVideoList.add(result.getInt(index))
                }
                binding.progressBar.visibility = View.GONE

                favouriteVideoList.forEach { videoId ->
                    val shuffledArrayList: ArrayList<Int> = arrayListOf(videoId)
                    shuffledArrayList.addAll(favouriteVideoList.filter {
                        it != videoId
                    }.shuffled())

                    addViewToFavourite(
                        videoId = videoId,
                        shuffledArrayList
                    )
                }
            }
        }, {
            Log.e("GetFavourites", "Error at sign in : " + it.message)
        })
        
        fillFavouritesQueue.add(getFavouritesRequest)
    }

    private fun dislikeVideo(videoId: Int) {
        val url = resources.getString(R.string.base_url) +
                "/likeVideo?" +
                "videoId=$videoId&" +
                "email=${userData.email}&" +
                "phone=${userData.phone}"

        val likeVideoQueue = Volley.newRequestQueue(requireContext())
        val videoLikeCountRequest = StringRequest(Request.Method.GET, url, {
            run {}
        }, {
            Log.e("LikeVideo", "Error at sign in : " + it.message)
        })

        likeVideoQueue.add(videoLikeCountRequest)
    }

    private fun addViewToFavourite(videoId: Int, favouriteVideoList: ArrayList<Int>) {
        val viewBinding = FavVideoItemBinding.inflate(
            layoutInflater,
            binding.favouriteLayout,
            true
        )

        viewBinding.root.setOnLongClickListener {
            Toast.makeText(
                requireContext(),
                "Video $videoId disliked", Toast.LENGTH_SHORT
            ).show()
            true
        }

        viewBinding.root.setOnClickListener {
            if (videoId != 0) {
                (requireActivity() as OpenVideoActivity).openFragment(
                    OpenVideoFragment.newInstance(
                        videoIdList = favouriteVideoList,
                        title = "Видео",
                        showMenuButtons = false,
                        showAd = false
                    )
                )
            }
        }

        viewBinding.button.setOnClickListener {
            binding.favouriteLayout.removeView(viewBinding.root)
            dislikeVideo(videoId)
            Toast.makeText(
                requireContext(),
                "Video $videoId disliked", Toast.LENGTH_SHORT
            ).show()
        }

        val urlSrc = resources.getString(R.string.res_url) + "/$videoId.jpg"
        val previewImage = viewBinding.imageView10

        Picasso
            .get()
            .load(urlSrc)
            .placeholder(R.drawable.rectangle34)
            .into(previewImage)

        val viewCountUrl = resources.getString(R.string.base_url) + "/getViewCount?videoId=$videoId"
        val viewQueue = Volley.newRequestQueue(requireContext())

        val viewCountRequest = StringRequest(Request.Method.GET, viewCountUrl, { response ->
            run {
                viewBinding.viewCount.text =
                    JSONObject(response).getInt("viewCount").toString()
            }
        }, {
            Log.e("ViewCount", "Error at sign in : " + it.message)
        })

        viewQueue.add(viewCountRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}