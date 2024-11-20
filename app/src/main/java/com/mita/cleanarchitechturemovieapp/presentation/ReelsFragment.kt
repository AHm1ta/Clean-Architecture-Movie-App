package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.mita.cleanarchitechturemovieapp.common.baseComponent.BaseFragment
import com.mita.cleanarchitechturemovieapp.data.model.DemoData
import com.mita.cleanarchitechturemovieapp.databinding.FragmentReelsBinding
import com.mita.cleanarchitechturemovieapp.presentation.adapter.ReelsAdapter


class ReelsFragment : BaseFragment<FragmentReelsBinding>() {
    private lateinit var adapter: ReelsAdapter
    private val viewModel: ReelsViewModel by viewModels()

    override fun viewBindingLayout(): FragmentReelsBinding =
        FragmentReelsBinding.inflate(layoutInflater)


    override fun initializeView(savedInstanceState: Bundle?) {
        viewModel.setReels(DemoData.reelsList)
        setupViewPager()
        observeViewModel()

    }

    private fun setupViewPager() {
        adapter = ReelsAdapter(
            viewModel,
            onShareClicked = { //reel -> shareReel(reel)
                },
            requireActivity().supportFragmentManager
        )

        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.setCurrentPosition(position)
            }
        })
    }

    private fun observeViewModel() {
        viewModel.reels.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter.releasePlayers()
    }

}