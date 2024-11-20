package com.mita.cleanarchitechturemovieapp.presentation.video

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.mita.cleanarchitechturemovieapp.databinding.FragmentVideoPlayerBinding
import java.io.File

class VideoPlayerFragment : Fragment() {
    private lateinit var binding: FragmentVideoPlayerBinding
    private lateinit var exoPlayer: ExoPlayer
    private var outputMergedFile: File? = null

    @OptIn(UnstableApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)

        val filePath = requireArguments().getString("outputFile")

        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer

        outputMergedFile = File(filePath!!)
        if (outputMergedFile!!.exists() && outputMergedFile!!.isFile){
            val mediaItem = MediaItem.fromUri(Uri.fromFile(outputMergedFile))

            val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(requireContext(), "ExoPlayer"))
                .createMediaSource(mediaItem)

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

//
//            val dataSourceFactory = DefaultHttpDataSource.Factory()
//            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(Uri.fromFile(outputMergedFile)))
//
//            exoPlayer.setMediaSource(mediaSource)
//            exoPlayer.prepare()
            exoPlayer.volume = 1.0f
            exoPlayer.play()
        }else{
            Toast.makeText(requireContext(), "Video not found", Toast.LENGTH_SHORT).show()
        }

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_READY || state == Player.STATE_BUFFERING) {
                    //updateSeekBar()
                    //
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(requireContext(), "Error: ${error.message}, ${error.errorCodeName}", Toast.LENGTH_SHORT).show()
            }

        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VideoPlayerFragment().apply {
            }
    }
}