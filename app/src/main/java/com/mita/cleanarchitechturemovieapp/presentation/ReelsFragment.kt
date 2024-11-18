package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.mita.cleanarchitechturemovieapp.common.baseComponent.BaseFragment
import com.mita.cleanarchitechturemovieapp.data.model.DemoData
import com.mita.cleanarchitechturemovieapp.databinding.FragmentReelsBinding
import com.mita.cleanarchitechturemovieapp.presentation.adapter.ReelsPagerAdapter
import com.mita.cleanarchitechturemovieapp.presentation.reels.Reels2Adapter
import com.mita.cleanarchitechturemovieapp.presentation.reels.Reels2ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReelsFragment : BaseFragment<FragmentReelsBinding>() {
    private var reelsAdapter: Reels2Adapter? = null
    private var reelsPagerAdapter: ReelsPagerAdapter? = null
    private lateinit var viewModel: Reels2ViewModel
    private var currentPlayingPosition = -1
    private var lastSelectedPosition = -1
    private val handler = Handler(Looper.getMainLooper())

    override fun viewBindingLayout(): FragmentReelsBinding =
        FragmentReelsBinding.inflate(layoutInflater)


    override fun initializeView(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[Reels2ViewModel::class.java]

        // Set the adapter
        reelsPagerAdapter = ReelsPagerAdapter(requireActivity(), DemoData.reelsList)
        binding.viewPager.adapter = reelsPagerAdapter

       // initView2()
    }

    private fun initView2() {
        reelsAdapter = Reels2Adapter(viewModel)
        binding.viewPager.adapter = reelsAdapter

        viewModel.reels.observe(this) {
            reelsAdapter!!.setReels(DemoData.reelsList)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position != lastSelectedPosition) {
                    lastSelectedPosition = position  // Update the last selected position
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(16)
                        reelsAdapter!!.releasePlayer()
                        reelsAdapter!!.playVideoAt(position)
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
      //  if (currentPlayingPosition >= 0) reelsAdapter?.playVideoAt(currentPlayingPosition)
    }

    override fun onPause() {
        super.onPause()
        // reelsAdapter?.releasePlayer()
    }


    override fun onDestroy() {
        super.onDestroy()
     //   reelsAdapter!!.releasePlayer()
    }

    /*private fun initView() {
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
                    reelsAdapter!!.setCurrentPlayingPosition(currentPlayingPosition)
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
    }*/

    private fun showLoading(isLoading: Boolean) {
      //  binding.loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    /* override fun onPause() {
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
 */


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