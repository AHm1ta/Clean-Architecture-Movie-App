package com.mita.cleanarchitechturemovieapp.presentation

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer

class ReelsViewModel : ViewModel() {
    private val playerInstances = mutableMapOf<Int, ExoPlayer>() // Store player instances by position
/*
    fun onPageChanged(newPosition: Int) {
        if (currentPlayingPosition != newPosition) {
            pausePlayer(currentPlayingPosition)
            playPlayer(newPosition)
            currentPlayingPosition = newPosition
        }
    }*/

    fun registerPlayer(position: Int, player: ExoPlayer) {
        playerInstances[position] = player // Store the ExoPlayer instance
    }

    fun releasePlayer(position: Int) {
        playerInstances[position]?.release() // Release the player when no longer needed
        playerInstances.remove(position) // Remove the reference from the map
    }

    fun pausePlayer(position: Int) {
        playerInstances[position]?.playWhenReady = false // Pause playback
    }

    fun playPlayer(position: Int) {
        playerInstances[position]?.apply {
            playWhenReady = true // Start playback
            seekToDefaultPosition() // Seek to the beginning if needed
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