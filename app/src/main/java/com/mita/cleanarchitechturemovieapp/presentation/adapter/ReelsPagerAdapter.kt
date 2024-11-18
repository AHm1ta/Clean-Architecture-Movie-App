package com.mita.cleanarchitechturemovieapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.presentation.VideoFragment

class ReelsPagerAdapter(
    fragment: Fragment,
    private val reelsItem: List<ReelsItem>,
) : FragmentStateAdapter(fragment) {

    private val players = mutableMapOf<Int, ExoPlayer>()

    override fun getItemCount(): Int = reelsItem.size

    override fun createFragment(position: Int): Fragment {
        val reelsItem = reelsItem[position]
        return VideoFragment.newInstance(reelsItem, position)
    }

    // Pause all players
    fun pauseAllPlayers() {
        players.values.forEach { it.pause() }
    }

    // Start the player for the given position
    fun startPlayerAtPosition(position: Int) {
        players[position]?.play()
    }

    // Release all players
    fun releaseAllPlayers() {
        players.values.forEach { it.release() }
        players.clear()
    }

    // Register a player for a specific position
    fun registerPlayer(position: Int, player: ExoPlayer) {
        players[position] = player
    }

    // Unregister a player for a specific position
    fun unregisterPlayer(position: Int) {
        players[position]?.release()
        players.remove(position)
    }
}