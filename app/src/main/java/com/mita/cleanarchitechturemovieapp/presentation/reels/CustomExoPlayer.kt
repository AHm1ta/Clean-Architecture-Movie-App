package com.mita.cleanarchitechturemovieapp.presentation.reels

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.text.TextAnnotation.Position
import androidx.media3.exoplayer.ExoPlayer
import timber.log.Timber

class CustomExoPlayer(context: Context) {
    var playWhenReady: Boolean = false
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    fun setMediaSource(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
    }

    fun prepare() {
        exoPlayer.prepare()
    }


    fun play() {
        exoPlayer.play()
        Timber.tag("VideoFragment").i("play")
    }

    fun pause() {
        exoPlayer.pause()
        Timber.tag("VideoFragment").i("pause")
    }

    fun release() {
        exoPlayer.release()
        Timber.tag("VideoFragment").i("release")
    }

    fun getPlayer(): ExoPlayer = exoPlayer
}