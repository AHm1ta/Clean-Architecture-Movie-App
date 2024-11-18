package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentVideoBinding.inflate(inflater, container, false)


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
        }

        binding.playerView.setOnTouchListener { _, _ ->
            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
            true
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        exoPlayer.playWhenReady = true
        Log.d("VideoFragment", "onStart called")
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.playWhenReady = false
        Log.d("VideoFragment", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.release()
        Log.d("VideoFragment", "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.release()
        Log.d("VideoFragment", "onDestroy called")
    }

    companion object {
        fun newInstance(reelsData: ReelsItem): VideoFragment {
            val fragment = VideoFragment()
            fragment.reelsItem = reelsData
            return fragment
        }
    }
}