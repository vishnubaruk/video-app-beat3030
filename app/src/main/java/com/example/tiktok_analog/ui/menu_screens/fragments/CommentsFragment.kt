package com.example.tiktok_analog.ui.menu_screens.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tiktok_analog.R
import com.example.tiktok_analog.data.model.User
import com.example.tiktok_analog.ui.OpenVideoActivity
import com.example.tiktok_analog.util.ViewPagerAdapter
import org.json.JSONObject
import java.lang.IllegalStateException

class CommentsFragment : Fragment(R.layout.fragment_comments) {
    lateinit var userData: User

    private lateinit var requestQueue: RequestQueue
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!
        rootView = view

        requireActivity().openFileInput("userData").use {
            userData = User.newUser(JSONObject(it.readBytes().toString(Charsets.UTF_8)))
        }

        requestQueue = Volley.newRequestQueue(requireActivity().applicationContext)

        view.findViewById<Button>(R.id.sendButton).setOnClickListener {
            addComment(view.findViewById<TextView>(R.id.commentText).text.toString(), view)
            view.findViewById<TextView>(R.id.commentText).text = ""
        }

        updateComments()
        return view
    }

    private fun addComment(commentText: String, view: View) {
        val url = "https://kepler88d.pythonanywhere.com/addComment?videoId=" +
                "${((requireActivity() as OpenVideoActivity).getViewPager2().adapter as ViewPagerAdapter).getCurrentVideoId()}" +
                "&commentText=${commentText.trim()}&email=${userData.email}&phone=${userData.phone}"

        val addCommentRequest = StringRequest(Request.Method.GET, url, {
            run {
                updateComments()
            }
        }, {
            Log.e("Add comment", "Error at sign in : " + it.message)
        })

        requestQueue.add(addCommentRequest)
    }

    public fun updateComments() {
        rootView.findViewById<LinearLayout>(R.id.commentsContainer).removeAllViews()

        val url =
            "https://kepler88d.pythonanywhere.com/getComments?videoId=" +
                    "${((requireActivity() as OpenVideoActivity).getViewPager2().adapter as ViewPagerAdapter).getCurrentVideoId()}"

        val commentRequest = StringRequest(Request.Method.GET, url, { response ->
            run {
                val result = JSONObject(response).getJSONArray("result")

                for (index in 0 until result.length()) {
                    addCommentView(result.getJSONObject(index), rootView)
                }
            }
        }, { Log.e("Comments", "Error at sign in : " + it.message) })

        requestQueue.add(commentRequest)
    }

    private fun addCommentView(jsonObject: JSONObject, view: View) {
        try {
            val newView =
                LayoutInflater.from(requireActivity().applicationContext)
                    .inflate(R.layout.comment_item, null, false)
            newView.findViewWithTag<TextView>("sender").text =
                jsonObject.getString("authorUsername")
            newView.findViewWithTag<TextView>("commentText").text =
                jsonObject.getString("text")
            view.findViewById<LinearLayout>(R.id.commentsContainer).addView(newView)

            newView.findViewWithTag<ImageView>("likeIcon").setOnClickListener {
                it.setBackgroundResource(R.drawable.ic_like)
            }

            view.findViewById<NestedScrollView>(R.id.commentsScrollView).post {
                view.findViewById<NestedScrollView>(R.id.commentsScrollView)
                    .fullScroll(ScrollView.FOCUS_DOWN)
            }
        } catch (e: IllegalStateException) {
            print(e.stackTrace)
        }
    }

    private fun hideKeyboard(activity: Activity) {
        (activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(
                (if (activity.currentFocus == null) View(activity)
                else activity.currentFocus)!!.windowToken, 0
            )
    }
}