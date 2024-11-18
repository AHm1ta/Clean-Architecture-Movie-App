package com.mita.cleanarchitechturemovieapp.presentation.reels

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.mita.cleanarchitechturemovieapp.R

class CustomExoPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(
    context, attrs,
    defStyleAttr
), Player.Listener {
    private var player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val playerView: PlayerView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_exo_player, this, true)
        playerView = view.findViewById(R.id.player_view)

        playerView.player = player
        playerView.useController = false
        player.addListener(this)
    }

    fun setVideoUrl(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun play() {
        player.playWhenReady = true
        Log.d("REELS", "Player play")
    }

    fun pause() {
        player.playWhenReady = false
        Log.d("REELS", "Player pause")
    }

    fun release() {
        player.removeListener(this)
        player.release()
        Log.d("REELS", "release Player")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        // Handle the change in playing state
        if (isPlaying) {
        } else {
            //   progressBar.visibility = VISIBLE
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        // Handle the change in player state
        when (playbackState) {
            Player.STATE_IDLE -> {
                // Player is idle
            }

            Player.STATE_BUFFERING -> {
            }

            Player.STATE_READY -> {
            }

            Player.STATE_ENDED -> {
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        //

    }

    override fun onRenderedFirstFrame() {
        super.onRenderedFirstFrame()
    }
}