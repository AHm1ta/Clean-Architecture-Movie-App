package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.mita.cleanarchitechturemovieapp.common.baseComponent.BaseFragment
import com.mita.cleanarchitechturemovieapp.data.model.DemoData
import com.mita.cleanarchitechturemovieapp.databinding.FragmentReelsBinding
import com.mita.cleanarchitechturemovieapp.presentation.adapter.ReelsPagerAdapter
import com.mita.cleanarchitechturemovieapp.presentation.reels.Reels2ViewModel


class ReelsFragment : BaseFragment<FragmentReelsBinding>() {
    lateinit var reelsPagerAdapter: ReelsPagerAdapter
    private lateinit var viewModel: Reels2ViewModel

    override fun viewBindingLayout(): FragmentReelsBinding =
        FragmentReelsBinding.inflate(layoutInflater)


    override fun initializeView(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[Reels2ViewModel::class.java]

        // Set the adapter
        reelsPagerAdapter = ReelsPagerAdapter(this, DemoData.reelsList)
        binding.viewPager.adapter = reelsPagerAdapter

        // Handle page change to manage playback
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                reelsPagerAdapter.pauseAllPlayers() // Pause all players
                reelsPagerAdapter.startPlayerAtPosition(position) // Play the current player
            }
        })
    }

    override fun onPause() {
        super.onPause()
        reelsPagerAdapter.pauseAllPlayers() // Pause all videos when the fragment pauses
    }

    override fun onDestroyView() {
        super.onDestroyView()
        reelsPagerAdapter.releaseAllPlayers() // Release ExoPlayer resources
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        fun newInstance(): ReelsFragment {
            val fragment = ReelsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}