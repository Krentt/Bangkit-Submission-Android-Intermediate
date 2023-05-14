package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.api.ListStoryItem
import com.example.storyapp.database.Story
import java.text.SimpleDateFormat
import java.util.*

class ListStoriesAdapter :
    PagingDataAdapter<Story, ListStoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClckCallback: OnItemClickCallback

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClckCallback = onItemClickCallback
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.iv_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDate: TextView = itemView.findViewById(R.id.tv_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_story, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val listStories = getItem(position)
        if (listStories != null ){
            val name = listStories.name

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            val dateResp = listStories.createdAt
            val parseDate = dateResp?.let { inputFormat.parse(it) }
            val date = parseDate?.let { outputFormat.format(it) }
            val photo = listStories.photoUrl
            Glide.with(holder.itemView.context)
                .load(photo)
                .into(holder.imgPhoto)
            holder.tvName.text = name
            holder.tvDate.text = date

            val listStoryItem = ListStoryItem(
                id = listStories.id,
                name = listStories.name,
                createdAt = listStories.createdAt,
                photoUrl = listStories.photoUrl
            )

            holder.itemView.setOnClickListener { onItemClckCallback.onItemClicked(listStoryItem) }
        }
    }
}