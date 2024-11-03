package com.mita.cleanarchitechturemovieapp.presentation.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.recyclerview.widget.RecyclerView
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.common.utils.NetworkUtils
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.databinding.ItemReelsLayoutBinding
import com.mita.cleanarchitechturemovieapp.presentation.ReelsViewModel

class ReelsAdapter(
    private val reelsList: List<ReelsItem>, // List of video URLs
    private val viewModel: ReelsViewModel,
    private val showLoading: (Boolean) -> Unit,// ViewModel to manage playback
) : RecyclerView.Adapter<ReelsAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(val binding: ItemReelsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var exoPlayer: ExoPlayer? = null


        /*init {
            // Set up touch listener to pause or resume playback
            binding.playerView.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        viewModel.userControllerOff() // Pause all players when the user interacts
                    }

                    MotionEvent.ACTION_UP -> {
                        viewModel.userControllerOn(absoluteAdapterPosition) // Resume the current player
                    }
                }
                true
            }
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding =
            ItemReelsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    @UnstableApi
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val reelsData = reelsList[position]

        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        // Initialize ExoPlayer for each video item
        holder.exoPlayer = ExoPlayer.Builder(holder.itemView.context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
                setMediaItem(MediaItem.fromUri(reelsData.videoUrl))

                playWhenReady = position == 0  // Play the first video by default
                holder.binding.playerView.player = this
                //repeatMode = Player.REPEAT_MODE_ONE


                showLoading(true)
                addListener(object : Player.Listener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            showLoading(false) // Hide loading when ready to play
                            holder.binding.playerView.useController = false
                        }
                        if (playbackState == Player.STATE_BUFFERING) {
                            showLoading(true) // Hide loading when ready to play
                            holder.binding.playerView.useController = false
                        }
                        if (playbackState == Player.STATE_ENDED) {
                            // Move to the next video
                            holder.binding.playerView.useController = true
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        showLoading(false) // Hide loading on error
                        if (NetworkUtils.isInternetAvailable(holder.binding.playerView.context)){
                            Toast.makeText(
                                holder.itemView.context,
                                "No internet connection. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else {
                            Toast.makeText(
                                holder.itemView.context,
                                "Error playing video: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                prepare()
                playWhenReady = true
            }
        // Register the player with the ViewModel
        viewModel.registerPlayer(position, holder.exoPlayer!!)

        holder.binding.likeButton.setOnClickListener {
            holder.binding.likeButton.setImageResource(R.drawable.ic_liked)
        }

        // Set reel information
        holder.binding.username.text = reelsData.username
        holder.binding.description.text = reelsData.description
        holder.binding.likesCount.text = reelsData.likesCount.toString()
        holder.binding.commentsCount.text = reelsData.commentsCount.toString()
    }

    override fun onViewRecycled(holder: VideoViewHolder) {
        super.onViewRecycled(holder)
        viewModel.releasePlayer(holder.absoluteAdapterPosition) // Release player when view is recycled
        holder.exoPlayer?.release() // Release ExoPlayer instance
    }

    override fun getItemCount() = reelsList.size
}