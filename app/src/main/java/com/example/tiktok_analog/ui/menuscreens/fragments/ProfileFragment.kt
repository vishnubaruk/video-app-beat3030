package com.example.tiktok_analog.ui.menuscreens.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.databinding.FragmentProfileBinding
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.util.RequestWorker
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var userData: User

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        requireActivity().openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        fillProfileData()

        binding.backArrowButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.backArrowButton.visibility =
            if (requireActivity() is OpenVideoActivity) View.GONE else View.VISIBLE

        binding.yourProfileTab.setOnClickListener {
            binding.yourProfileTab.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(), R.color.buttonEnabledBg
            )
            binding.yourProfileTab.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            binding.yourVideosTab.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.groupUnselected)
            binding.yourVideosTab.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorPrimary
                )
            )

            binding.yourProfileBlock.visibility = View.VISIBLE
            binding.yourVideosBlock.visibility = View.GONE

            binding.sectionTitleText.text = "Ваш профиль"
        }

        binding.yourVideosTab.setOnClickListener {
            binding.yourProfileTab.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.groupUnselected)
            binding.yourProfileTab
                .setTextColor(resources.getColor(R.color.colorPrimary))

            binding.yourVideosTab.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.buttonEnabledBg)
            binding.yourVideosTab.setTextColor(resources.getColor(R.color.white))

            binding.yourProfileBlock.visibility = View.GONE
            binding.yourVideosBlock.visibility = View.VISIBLE

            binding.sectionTitleText.text = "Ваши видео"

            updateData()
        }

        binding.editUsername.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle("Введите имя пользователя")
                    .setView(
                        LayoutInflater.from(requireContext())
                            .inflate(R.layout.user_input_field, null, false)
                    )
                    .setPositiveButton("Применить") { _, _ -> run {} }
                    .setNegativeButton("Отмена") { _, _ -> run {} }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        }

        binding.editBirthDate.setOnClickListener {
            val cal: Calendar = Calendar.getInstance();
            val year: Int = cal.get(Calendar.YEAR);
            val month: Int = cal.get(Calendar.MONTH);
            val day: Int = cal.get(Calendar.DAY_OF_MONTH);

            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.birthDateText.text =
                        "${if (day < 10) "0" else ""}$day.${if (month < 9) "0" else ""}${month + 1}.$year"
                },
                year, month, day
            )

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -18)
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }

        binding.editCity.setOnClickListener {
            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle("Введите город пользователя")
                    .setView(
                        LayoutInflater.from(requireContext())
                            .inflate(R.layout.user_input_field, null, false)
                    )
                    .setPositiveButton("Применить") { _, _ -> run {} }
                    .setNegativeButton("Отмена") { _, _ -> run {} }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
        }

        binding.profileSwipeRefresh.setOnRefreshListener {
            binding.profileSwipeRefresh.isRefreshing = false
            Toast.makeText(
                requireActivity().applicationContext,
                "Profile page refreshed", Toast.LENGTH_SHORT
            ).show()

            updateData()
        }

        return binding.root
    }

    private fun updateData() {
        val url = resources.getString(R.string.base_url) +
                "/getUploadedVideosStats?" +
                "email=${userData.email}&" +
                "phone=${userData.phone}"

        val videoStatsQueue = Volley.newRequestQueue(requireActivity().applicationContext)
        val addCommentRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response)
                binding.videoCount.text =
                    result.getInt("videoCount").toString()
                binding.videoLikeCount.text =
                    result.getInt("likeCount").toString()
                binding.videoViewCount.text =
                    result.getInt("viewCount").toString()
            }
        }, {
            Log.e("VideoStats", "Error at sign in : " + it.message)
        })

        videoStatsQueue.add(addCommentRequest)
        binding.uploadedVideosLayout.removeAllViews()

        val uploadedVideosUrl = "https://kepler88d.pythonanywhere.com" +
                "/getUploadedVideos?" +
                "email=${userData.email}&" +
                "phone=${userData.phone}"

        val uploadedVideosQueue = Volley.newRequestQueue(requireActivity().applicationContext)
        val uploadedVideosRequest =
            StringRequest(Request.Method.GET, uploadedVideosUrl, { response ->
                run {
                    val result = JSONObject(response).getJSONArray("result")
                    for (index in 0 until result.length()) {
                        addViewToUploadedVideos(
                            videoId = result.getInt(index)
                        )
                    }
                }
            }, {
                Log.e("UploadedVideos", "Error at sign in : " + it.message)
            })

        uploadedVideosQueue.add(uploadedVideosRequest)
    }

    private fun addViewToUploadedVideos(videoId: Int) {
        val newView = LayoutInflater.from(requireActivity().applicationContext)
            .inflate(R.layout.fav_video_item, null, false)

        newView.setOnLongClickListener {
            Toast.makeText(
                requireActivity().applicationContext,
                "Video $videoId disliked",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        newView.setOnClickListener {
            if (videoId != 0) {
                startActivity(
                    Intent(
                        requireActivity().applicationContext,
                        OpenVideoActivity::class.java
                    ).putIntegerArrayListExtra("id", arrayListOf(videoId))
                )
            }
        }

        newView.findViewWithTag<Button>("delete")
            .setOnClickListener { deleteVideo(videoId, newView) }

        Picasso
            .get()
            .load(resources.getString(R.string.res_url) + "/$videoId.jpg")
            .placeholder(R.drawable.rectangle34)
            .into(newView.findViewWithTag<ImageView>("previewImage"))

        val viewCountUrl = resources.getString(R.string.base_url) +
                "/getViewCount?" +
                "videoId=$videoId"
        val viewQueue = Volley.newRequestQueue(requireActivity().applicationContext)

        val viewCountRequest = StringRequest(Request.Method.GET, viewCountUrl, { response ->
            run {
                val result = JSONObject(response)
                newView.findViewWithTag<TextView>("viewCount").text =
                    result.getInt("viewCount").toString()
            }
        }, {
            Log.e("ViewCount", "Error at sign in : " + it.message)
        })

        viewQueue.add(viewCountRequest)
        binding.uploadedVideosLayout.addView(newView)
    }

    private fun deleteVideo(videoId: Int, view: View) {
        val alertDialog = AlertDialog.Builder(requireContext()).setTitle("Удаление видео")
            .setMessage("Вы уверены, что хотите удалить данное видео?")
            .setPositiveButton("Да") { dialog, _ ->
                run {
                    RequestWorker.deleteVideo(videoId)
                    binding.uploadedVideosLayout.removeView(view)
                    dialog.dismiss()
                }
            }.setNegativeButton("Нет, отмена") { dialog, _ -> dialog.cancel() }
            .create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun fillProfileData() {
        binding.nameText.text = userData.username
        binding.nameTextHeader.text = userData.username

        binding.phoneText.text = userData.phone
        binding.birthDateText.text = userData.birthDate
        binding.cityText.text = userData.city

        binding.emailText.text = userData.email
        binding.emailTextHeader.text = userData.email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}