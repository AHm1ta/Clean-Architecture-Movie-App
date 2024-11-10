package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.common.baseComponent.BaseFragment
import com.mita.cleanarchitechturemovieapp.common.utils.NetworkUtils
import com.mita.cleanarchitechturemovieapp.data.model.DemoData
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem
import com.mita.cleanarchitechturemovieapp.databinding.FragmentMovieListBinding
import com.mita.cleanarchitechturemovieapp.databinding.FragmentReelsBinding
import com.mita.cleanarchitechturemovieapp.presentation.adapter.ReelsAdapter

class ReelsFragment : BaseFragment<FragmentReelsBinding>() {
    private var reelsAdapter: ReelsAdapter? = null
    private lateinit var viewModel: ReelsViewModel
    private var currentPlayingPosition = -1

    override fun viewBindingLayout(): FragmentReelsBinding =
        FragmentReelsBinding.inflate(layoutInflater)


    override fun initializeView(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[ReelsViewModel::class.java]

        initView()
    }

    private fun initView() {
        if (NetworkUtils.isInternetAvailable(requireContext())) {
            showLoading(true)
            reelsAdapter = ReelsAdapter(DemoData.reelsList, viewModel, ::showLoading, currentPlayingPosition)
            binding.viewPager.adapter = reelsAdapter

            // Handle swipe transitions and playback state
            binding.viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    //viewModel.onPageChanged(position)
                    if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                        viewModel.pausePlayer(currentPlayingPosition)
                    }
                    // Play the video for the new position
                    viewModel.playPlayer(position)

                    // Update the current playing position
                    currentPlayingPosition = position
                }
            })
            showLoading(false)
        } else {
            showLoading(false)
            Toast.makeText(
                requireContext(),
                "No internet connection. Please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer(currentPlayingPosition)
    }

    override fun onStop() {
        super.onStop()
        viewModel.pausePlayer(currentPlayingPosition)
    }

    override fun onResume() {
        super.onResume()
        viewModel.playPlayer(currentPlayingPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer(currentPlayingPosition)
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