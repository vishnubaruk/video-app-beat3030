package com.example.tiktok_analog.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.item_page.view.*

class ViewPagerAdapter(private val videoIdList: List<Int>) : RecyclerView.Adapter<PagerVH>() {
    //    public lateinit var videoView: VideoView

    private val colors = intArrayOf(
        android.R.color.black,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    override fun getItemCount(): Int = videoIdList.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
//        videoView = this.findViewWithTag("videoView")
        container.setBackgroundColor(colors.random())
        this.findViewWithTag<TextView>("text").text = videoIdList[position].toString()
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
