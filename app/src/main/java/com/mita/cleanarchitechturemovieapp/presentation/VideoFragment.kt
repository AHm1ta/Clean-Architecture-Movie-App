package com.mita.cleanarchitechturemovieapp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {
    private lateinit var exoPlayer: ExoPlayer
    private var reelsItem: ReelsItem? = null
    private var position: Int = 0
    private lateinit var binding: FragmentVideoBinding

    private var isUserInteracting = false // Track user interaction with SeekBar
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVideoBinding.inflate(inflater, container, false)


        // Initialize ExoPlayer
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer

        binding.likeButton.setOnClickListener {
            binding.likeButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        }

        binding.followIcon.setOnClickListener {
            binding.followIcon.setImageResource(R.drawable.followed_bg)
        }

        // Set reel information
        binding.username.text = reelsItem?.username ?: ""
        binding.description.text = reelsItem?.description ?: ""
        binding.likesCount.text = reelsItem?.likesCount.toString()
        binding.commentsCount.text = reelsItem?.commentsCount.toString()

        // Get video URL from arguments
        val videoUrl = reelsItem?.videoUrl
        videoUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL// Loop playback
            exoPlayer.prepare()
            exoPlayer.play()
        }

        // Handle SeekBar changes
        binding.customPlayer.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isUserInteracting = true
                    exoPlayer.seekTo(progress.toLong())
                    updateSeekBarWithTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Show SeekBar and Play/Pause button when the user starts interacting
                isUserInteracting = true
                //exoPlayer.pause()
                binding.customPlayer.timeLay.visibility= View.VISIBLE
              //  binding.customPlayer.playPauseButton.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Hide SeekBar after the user stops interacting
                binding.customPlayer.timeLay.visibility= View.GONE
                binding.customPlayer.playPauseButton.visibility= View.GONE
                isUserInteracting = false
                exoPlayer.play()
            }
        })

        // Update the SeekBar and time TextView as the video plays
        exoPlayer.addListener(object : Player.Listener {
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
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                binding.customPlayer.playPauseButton.visibility= View.VISIBLE
            } else {
                exoPlayer.play()
                binding.customPlayer.playPauseButton.visibility= View.GONE
            }

            true
        }

        // Register player with adapter
        //(parentFragment as? ReelsFragment)?.reelsPagerAdapter?.registerPlayer(position, exoPlayer)

        binding.commentButton.setOnClickListener {
            showDialogFragment()
        }

        return binding.root
    }

    private fun showDialogFragment() {
       // resizePlayerView(isSmall = true) // Resize and reposition the PlayerView
        val layoutParams = binding.playerView.layoutParams as FrameLayout.LayoutParams
        binding.infoLay.visibility= View.GONE
        binding.customPlayer.root.visibility= View.GONE
        binding.customPlayer.root.visibility= View.GONE
        binding.toolbar.visibility= View.GONE
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
                binding.playerView.translationY = -binding.playerView.height.toFloat() // Start above the screen
                binding.playerView.alpha = 0f // Start fully transparent
                binding.playerView.visibility = View.VISIBLE // Ensure it's visible before animation
            }
            .start()
        binding.playerView.layoutParams = layoutParams

        val dialogFragment = CommentDialogFragment {
            resizePlayerView()
        }
        dialogFragment.show(requireActivity().supportFragmentManager, "ExampleDialog")
        // Delay matches animation duration
    }

    private fun resizePlayerView() {
        val layoutParams = binding.playerView.layoutParams as FrameLayout.LayoutParams
            binding.infoLay.visibility= View.VISIBLE
            binding.customPlayer.root.visibility= View.VISIBLE
            binding.customPlayer.root.visibility= View.VISIBLE
            binding.toolbar.visibility= View.VISIBLE
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
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun startSeekBarUpdater() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isUserInteracting) {
                    val currentPosition = exoPlayer.currentPosition
                    binding.customPlayer.seekBar.max = exoPlayer.duration.toInt()
                    binding.customPlayer.seekBar.progress = currentPosition.toInt()
                    updateSeekBarWithTime(currentPosition)
                }
                handler.postDelayed(this, 1000) // Update every second
            }
        })
    }

    private fun updateSeekBarWithTime(currentPosition: Long) {
        val totalDuration = exoPlayer.duration
        val currentFormatted = formatTime(currentPosition)
        val totalFormatted = formatTime(totalDuration)

        binding.customPlayer.timePosition.text = "$currentFormatted"
        binding.customPlayer.timeDuration.text = " / $totalFormatted"
    }

    private fun formatTime(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause() // Pause playback when fragment pauses
        Log.d("VideoFragment", "onPause called")
    }

    override fun onResume() {
        super.onResume()
        exoPlayer.playWhenReady = true
    }
    override fun onDestroyView() {
        super.onDestroyView()
       // (parentFragment as? ReelsFragment)?.reelsPagerAdapter?.unregisterPlayer(position ?: -1)
        exoPlayer.release() // Release ExoPlayer resources
        handler.removeCallbacksAndMessages(null) // Stop updates
        Log.d("VideoFragment", "onDestroyView called")
    }

    override fun onStart() {
        super.onStart()
        exoPlayer.playWhenReady = true
        Log.d("VideoFragment", "onStart called")
    }


    override fun onStop() {
        super.onStop()
        exoPlayer.release()
        Log.d("VideoFragment", "onStop called")
    }



    companion object {
        fun newInstance(reelsData: ReelsItem, position: Int): VideoFragment {
            val fragment = VideoFragment()
            fragment.reelsItem = reelsData
            fragment.position = position
            return fragment
        }
    }
}