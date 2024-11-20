package com.mita.cleanarchitechturemovieapp.presentation.reels

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.databinding.ItemReelsLayoutBinding

class Reels2Adapter(private val viewModel: Reels2ViewModel) : RecyclerView.Adapter<Reels2Adapter.ReelViewHolder>() {

    private var reels: List<ReelsItem> = emptyList()
    private var currentPlayingPosition: Int = -1
    private var currentPlayer: CustomExoPlayer? = null

    fun setReels(reels: List<ReelsItem>) {
        this.reels = reels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val binding = ItemReelsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val reel = reels[position]
        holder.bind(reel, position)
    }

    override fun getItemCount(): Int = reels.size

    inner class ReelViewHolder(val binding: ItemReelsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reel: ReelsItem, position: Int) {

           /* binding.playerView.setVideoUrl(reel.videoUrl)

            if (position == currentPlayingPosition) {
                currentPlayer = binding.playerView
                binding.playerView.play()
            } else {
                binding.playerView.pause()
            }
*/
            binding.likeButton.setOnClickListener {
                viewModel.likeReel(reel.id)
                binding.likeButton.setImageResource(R.drawable.ic_liked)
            }

            binding.username.text = reel.username
            binding.description.text = reel.description
            binding.likesCount.text = reel.likesCount.toString()
            binding.commentsCount.text = reel.commentsCount.toString()

        }
    }

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)
       // holder.binding.playerView.release()
    }

    fun releasePlayer() {
        currentPlayer?.release()
        currentPlayer = null

    }

    /*fun playVideoAt(position: Int) {
        currentPlayingPosition = position
        notifyDataSetChanged()
        Log.d("REELS", "playVideoAt $position")

    }*/
    fun playVideoAt(position: Int) {
        val previousPosition = currentPlayingPosition
        currentPlayingPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(currentPlayingPosition)
    }
}
