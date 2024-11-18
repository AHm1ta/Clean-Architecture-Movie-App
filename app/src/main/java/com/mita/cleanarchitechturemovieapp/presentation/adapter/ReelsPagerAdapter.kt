package com.mita.cleanarchitechturemovieapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.presentation.VideoFragment

class ReelsPagerAdapter (fragmentActivity: FragmentActivity,
                         private val reelsItem: List<ReelsItem>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = reelsItem.size

    override fun createFragment(position: Int): Fragment {
        val reelsItem = reelsItem[position]
        return VideoFragment.newInstance(reelsItem)
    }
}