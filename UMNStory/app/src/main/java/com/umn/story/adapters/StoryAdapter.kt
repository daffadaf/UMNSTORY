package com.umn.story.adapters

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umn.story.R
import com.umn.story.databinding.ItemStoryBinding
import com.umn.story.models.StoryUser
import java.text.SimpleDateFormat
import java.util.Locale

class StoryAdapter(private val listener: StoryAdapterListener) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    private var storyList: List<StoryUser> = ArrayList()

    fun setList(storyList: List<StoryUser>) {
        this.storyList = storyList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val bind: ItemStoryBinding) : RecyclerView.ViewHolder(bind.root) {
        fun bindData(story: StoryUser) {
            bind.tvCerita.text = story.story.cerita
            bind.tvTanggal.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(story.story.createdAt!!)
            story.story.foto?.let{
                Glide.with(bind.root.context).load(story.story.foto).into(bind.ivStory)
            }?:run{
                bind.ivStory.visibility = View.GONE
            }
            Glide.with(bind.root.context).load(if (story.like) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24).into(bind.ivLike)
            bind.ivLike.setOnClickListener{
                story.like = !story.like
                listener.onLike(story)
                Glide.with(bind.root.context).load(if (story.like) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24).into(bind.ivLike)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storyList[position]
        holder.bindData(story)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    interface StoryAdapterListener {
        fun onLike(story: StoryUser)
    }
}