package com.example.storyapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.api.ListStoryItem
import java.text.SimpleDateFormat
import java.util.*

class ListStoriesAdapter(private val listStories: List<ListStoryItem>) :
    RecyclerView.Adapter<ListStoriesAdapter.ViewHolder>() {

    private lateinit var onItemClckCallback: OnItemClickCallback

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

    override fun getItemCount(): Int {
        Log.d("ListStoriesAdapter", listStories.size.toString())
        return listStories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val name = listStories[position].name

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        val dateResp = listStories[position].createdAt
        val parseDate = dateResp?.let { inputFormat.parse(it) }
        val date = parseDate?.let { outputFormat.format(it) }
        val photo = listStories[position].photoUrl
        Glide.with(holder.itemView.context)
            .load(photo)
            .into(holder.imgPhoto)
        holder.tvName.text = name
        holder.tvDate.text = date

        holder.itemView.setOnClickListener { onItemClckCallback.onItemClicked(listStories[holder.adapterPosition]) }
    }
}