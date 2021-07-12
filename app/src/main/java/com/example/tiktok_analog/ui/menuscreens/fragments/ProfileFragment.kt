package com.example.tiktok_analog.ui.menuscreens.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.databinding.FavVideoItemBinding
import com.example.tiktok_analog.databinding.FragmentProfileBinding
import com.example.tiktok_analog.databinding.UserInputFieldBinding
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.ui.afterTextChanged
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

        userData = readUserData()
        fillProfileData(userData)
        updateData()

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
            val inputFieldBinding = UserInputFieldBinding.inflate(
                layoutInflater, null, false
            )

            inputFieldBinding.editText.setText(userData.username)

            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle("Введите имя пользователя")
                    .setView(
                        inputFieldBinding.root
                    )
                    .setPositiveButton("Применить") { _, _ ->
                        run {
                            binding.nameText.text = inputFieldBinding.editText.text.toString()
                            RequestWorker.editUserName(
                                userData.userId.toInt(),
                                inputFieldBinding.editText.text.toString()
                            ) { updateData() }
                        }
                    }
                    .setNegativeButton("Отмена") { _, _ -> run {} }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false

            inputFieldBinding.editText.afterTextChanged {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                    inputFieldBinding.editText.text.toString().isNotEmpty()
            }
        }

        binding.editBirthDate.setOnClickListener {
            val cal: Calendar = Calendar.getInstance();
            val year: Int = cal.get(Calendar.YEAR);
            val month: Int = cal.get(Calendar.MONTH);
            val day: Int = cal.get(Calendar.DAY_OF_MONTH);

            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val formattedBirthDate =
                        "${if (day < 10) "0" else ""}$day.${if (month < 9) "0" else ""}${month + 1}.$year"
                    binding.birthDateText.text = formattedBirthDate
                    RequestWorker.editUserBirthDate(
                        userData.userId.toInt(),
                        formattedBirthDate
                    ) { updateData() }
                },
                year, month, day
            )

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -18)
            dialog.datePicker.maxDate = calendar.timeInMillis
            dialog.show()
        }

        binding.editCity.setOnClickListener {
            val inputFieldBinding = UserInputFieldBinding.inflate(
                layoutInflater, null, false
            )

            inputFieldBinding.editText.setText(userData.city)

            val alertDialog =
                AlertDialog.Builder(requireContext()).setTitle("Название вашего города")
                    .setView(inputFieldBinding.root)
                    .setPositiveButton("Применить") { _, _ ->
                        run {
                            binding.cityText.text = inputFieldBinding.editText.text.toString()
                            RequestWorker.editUserCity(
                                userData.userId.toInt(),
                                inputFieldBinding.editText.text.toString()
                            ) { updateData() }
                        }
                    }
                    .setNegativeButton("Отмена") { _, _ -> run {} }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false

            inputFieldBinding.editText.afterTextChanged {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                    inputFieldBinding.editText.text.toString().isNotEmpty()
            }
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

        val uploadedVideoList = arrayListOf<Int>()
        val uploadedVideosRequest =
            StringRequest(Request.Method.GET, uploadedVideosUrl, { response ->
                run {
                    val result = JSONObject(response).getJSONArray("result")
                    for (index in 0 until result.length()) {
                        uploadedVideoList.add(result.getInt(index))
                    }
                }
            }, {
                Log.e("UploadedVideos", "Error at sign in : " + it.message)
            })

        uploadedVideoList.forEach { videoId ->
            val shuffledArrayList: ArrayList<Int> = arrayListOf<Int>()
            shuffledArrayList.addAll(
                uploadedVideoList.filter {
                    it != videoId
                }.shuffled()
            )

            addViewToUploadedVideos(
                videoId = videoId,
                shuffledArrayList
            )
        }

        uploadedVideosQueue.add(uploadedVideosRequest)
        RequestWorker.getUser(userData.userId.toInt()) { data ->
            requireActivity().runOnUiThread {
                userData = data
                writeUserData(data)
                fillProfileData(data)
            }
        }
    }

    private fun addViewToUploadedVideos(videoId: Int, uploadedVideoList: ArrayList<Int>) {
        val viewBinding = FavVideoItemBinding.inflate(
            layoutInflater,
            binding.uploadedVideosLayout,
            true
        )

        viewBinding.root.setOnLongClickListener {
            Toast.makeText(
                requireActivity().applicationContext,
                "Video $videoId disliked",
                Toast.LENGTH_SHORT
            ).show()
            true
        }

        viewBinding.root.setOnClickListener {
            if (videoId != 0) {
                (requireActivity() as OpenVideoActivity).openFragment(
                    OpenVideoFragment.newInstance(
                        videoIdList = uploadedVideoList,
                        title = "Видео",
                        showMenuButtons = false,
                        showAd = false
                    )
                )
            }
        }

        viewBinding.button.setOnClickListener { deleteVideo(videoId, viewBinding.root) }

        Picasso
            .get()
            .load(resources.getString(R.string.res_url) + "/$videoId.jpg")
            .placeholder(R.drawable.rectangle34)
            .into(viewBinding.imageView10)

        val viewCountUrl = resources.getString(R.string.base_url) +
                "/getViewCount?" +
                "videoId=$videoId"

        Volley.newRequestQueue(requireActivity().applicationContext).add(
            StringRequest(Request.Method.GET, viewCountUrl, { response ->
                run {
                    val result = JSONObject(response)
                    viewBinding.viewCount.text = result.getInt("viewCount").toString()
                }
            }, {
                Log.e("ViewCount", "Error at sign in : " + it.message)
            })
        )
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

    private fun readUserData(): User {
        requireActivity().openFileInput("userData").use {
            return User.fromJson(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }
    }

    private fun writeUserData(data: User): Unit {
        requireActivity().openFileOutput("userData", Context.MODE_PRIVATE)
            .write(data.toJsonString().toByteArray())
    }

    private fun fillProfileData(user: User) {
        binding.nameText.text = user.username
        binding.nameTextHeader.text = user.username

        binding.phoneText.text = user.phone
        binding.birthDateText.text = user.birthDate
        binding.cityText.text = user.city

        binding.emailText.text = user.email
        binding.emailTextHeader.text = user.email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}