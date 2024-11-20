package com.mita.cleanarchitechturemovieapp.presentation.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.media3.common.Player
import androidx.recyclerview.widget.RecyclerView
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.databinding.ItemReelsLayoutBinding
import com.mita.cleanarchitechturemovieapp.presentation.CommentDialogFragment
import com.mita.cleanarchitechturemovieapp.presentation.ReelsViewModel
import com.mita.cleanarchitechturemovieapp.presentation.reels.CustomExoPlayer

class ReelsAdapter(
    private val viewModel: ReelsViewModel,
    private val onShareClicked: (ReelsItem) -> Unit,
    private val fragmentManager: FragmentManager,
) : RecyclerView.Adapter<ReelsAdapter.VideoViewHolder>() {

    private val exoPlayerList = mutableListOf<CustomExoPlayer>()
    private var isUserInteracting = false // Track user interaction with SeekBar
    private val handler = Handler(Looper.getMainLooper())
    private var exoPlayer: CustomExoPlayer? = null

    inner class VideoViewHolder(val binding: ItemReelsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reel: ReelsItem, position: Int) {
            exoPlayer = CustomExoPlayer(binding.playerView.context).apply {
                setMediaSource(reel.videoUrl)
                prepare()
            }

            exoPlayerList.add(exoPlayer!!)
            binding.playerView.player = exoPlayer!!.getPlayer()
            exoPlayer!!.getPlayer().play()

            binding.likeButton.setOnClickListener {
                binding.likeButton.setColorFilter(
                    ContextCompat.getColor(
                        binding.likeButton.context,
                        R.color.red
                    )
                )
            }

            binding.followIcon.setOnClickListener {
                binding.followIcon.setImageResource(R.drawable.followed_bg)
            }

            // Set reel information
            binding.username.text = reel.username
            binding.description.text = reel.description
            binding.likesCount.text = reel.likesCount.toString()
            binding.commentsCount.text = reel.commentsCount.toString()

            binding.commentButton.setOnClickListener {
                val layoutParams = binding.playerView.layoutParams as FrameLayout.LayoutParams
                binding.infoLay.visibility = View.GONE
                binding.customPlayer.root.visibility = View.GONE
                binding.customPlayer.root.visibility = View.GONE
                binding.toolbar.visibility = View.GONE
                layoutParams.width = dpToPx(150)
                layoutParams.height = dpToPx(200)
                layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                val easeInOutInterpolator = PathInterpolator(0.42f, 0f, 0.58f, 1f)
                binding.playerView.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(500)
                    .setInterpolator(easeInOutInterpolator)
                    .withStartAction {
                        binding.playerView.translationY =
                            -binding.playerView.height.toFloat() // Start above the screen
                        binding.playerView.alpha = 0f // Start fully transparent
                        binding.playerView.visibility =
                            View.VISIBLE // Ensure it's visible before animation
                    }
                    .start()
                binding.playerView.layoutParams = layoutParams

                val dialogFragment = CommentDialogFragment {
                    resizePlayerView()
                }
                dialogFragment.show(fragmentManager, "ExampleDialog")
                // onCommentClicked(reel)
            }
            binding.shareButton.setOnClickListener { onShareClicked(reel) }

            // Handle SeekBar changes
            binding.customPlayer.seekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    if (fromUser) {
                        isUserInteracting = true
                        exoPlayer!!.getPlayer().seekTo(progress.toLong())
                        updateSeekBarWithTime(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    isUserInteracting = true
                    binding.customPlayer.timeLay.visibility = View.VISIBLE
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    binding.customPlayer.timeLay.visibility = View.GONE
                    binding.customPlayer.playPauseButton.visibility = View.GONE
                    isUserInteracting = false
                    exoPlayer!!.getPlayer().play()
                }
            })

            // Update the SeekBar and time TextView as the video plays
            exoPlayer!!.getPlayer().addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)
                    if (state == Player.STATE_READY || state == Player.STATE_BUFFERING) {
                        //updateSeekBar()
                        startSeekBarUpdater()
                    }
                }

                /*override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    if (playbackState == Player.STATE_READY) {
                        updateSeekBar()
                    }
                }*/
            })
            // Show controls when player view is touched
            binding.playerView.setOnClickListener {
                if (exoPlayer!!.getPlayer().isPlaying) {
                    exoPlayer!!.getPlayer().pause()
                    binding.customPlayer.playPauseButton.visibility = View.VISIBLE
                } else {
                    exoPlayer!!.getPlayer().play()
                    binding.customPlayer.playPauseButton.visibility = View.GONE
                }

                true
            }


        }

        private fun resizePlayerView() {
            val layoutParams = binding.playerView.layoutParams as FrameLayout.LayoutParams
            binding.infoLay.visibility = View.VISIBLE
            binding.customPlayer.root.visibility = View.VISIBLE
            binding.customPlayer.root.visibility = View.VISIBLE
            binding.toolbar.visibility = View.VISIBLE
            layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT
            layoutParams.gravity = Gravity.CENTER
            binding.playerView.animate()
                .translationY(0f) // Restore to original position
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            binding.playerView.layoutParams = layoutParams
        }

        private fun dpToPx(dp: Int): Int {
            return (dp * binding.playerView.context.resources.displayMetrics.density).toInt()
        }

        private fun startSeekBarUpdater() {
            handler.post(object : Runnable {
                override fun run() {
                    if (!isUserInteracting) {
                        val currentPosition = exoPlayer!!.getPlayer().currentPosition
                        binding.customPlayer.seekBar.max = exoPlayer!!.getPlayer().duration.toInt()
                        binding.customPlayer.seekBar.progress = currentPosition.toInt()
                        updateSeekBarWithTime(currentPosition)
                    }
                    handler.postDelayed(this, 1000) // Update every second
                }
            })
        }

        @SuppressLint("SetTextI18n")
        private fun updateSeekBarWithTime(currentPosition: Long) {
            val totalDuration = exoPlayer!!.getPlayer().duration
            val currentFormatted = formatTime(currentPosition)
            val totalFormatted = formatTime(totalDuration)

            binding.customPlayer.timePosition.text = currentFormatted
            binding.customPlayer.timeDuration.text = " / $totalFormatted"
        }


    }


    private fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    fun releasePlayers() {
        exoPlayerList.forEach { it.release() }
        exoPlayerList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding =
            ItemReelsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val reel = viewModel.reels.value!![position]
        holder.bind(reel, position)

    }


    override fun getItemCount() = viewModel.reels.value?.size ?: 0
}