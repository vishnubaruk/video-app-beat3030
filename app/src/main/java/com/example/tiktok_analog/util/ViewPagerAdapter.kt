package com.example.tiktok_analog.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok_analog.R
import kotlinx.android.synthetic.main.item_page.view.*

class ViewPagerAdapter(link: String) : RecyclerView.Adapter<PagerVH>() {

    private val colors = intArrayOf(
        android.R.color.black,
        android.R.color.holo_red_light,
        android.R.color.holo_blue_dark,
        android.R.color.holo_purple
    )

    public lateinit var videoView: VideoView
    private var videoLink: String = link

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
        videoView = this.findViewWithTag("videoView")
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)
