package com.mita.cleanarchitechturemovieapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer

class ReelsViewModel : ViewModel() {
    private val playerInstances =
        mutableMapOf<Int, ExoPlayer>() // Store player instances by position
    private var currentPlayingPosition: Int = -1

    fun onPageChanged(newPosition: Int) {
        if (currentPlayingPosition != newPosition) {
            pausePlayer(currentPlayingPosition)
            playPlayer(newPosition)
            currentPlayingPosition = newPosition
            Log.d("REELS", "onPageChanged , position: $newPosition")
        }
    }

    fun registerPlayer(position: Int, player: ExoPlayer) {
        playerInstances[position] = player // Store the ExoPlayer instance
        Log.d("REELS", "register Player")
    }

    fun releasePlayer(position: Int) {
        /*playerInstances[position]?.release() // Release the player when no longer needed
        playerInstances.remove(position)*/ // Remove the reference from the map
        playerInstances[position]?.apply {
            stop() // Stop the player
            release() // Release the player resources (audio/video buffers)
        }
        playerInstances.remove(position)
        Log.d("REELS", "release player,  position: $position")
    }

    fun pausePlayer(position: Int) {
        playerInstances[position]?.playWhenReady = false // Pause playback
    }

    fun playPlayer(position: Int) {
        playerInstances[position]?.apply {
            playWhenReady = true // Start playback
            seekToDefaultPosition() // Seek to the beginning if needed
            Log.d("REELS", "play player , position: $position")
        }
    }

    fun userControllerOff() {
        // Pauses all players
        playerInstances.forEach { (_, player) -> player.pause() }
    }

    fun userControllerOn(position: Int) {
        // Resumes playback for the specified position
        playerInstances[position]?.play()
    }
}