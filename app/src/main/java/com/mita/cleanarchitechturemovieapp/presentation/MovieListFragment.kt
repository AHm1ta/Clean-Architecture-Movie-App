package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.common.baseComponent.BaseFragment
import com.mita.cleanarchitechturemovieapp.common.utils.Resource
import com.mita.cleanarchitechturemovieapp.databinding.FragmentMovieListBinding
import com.mita.cleanarchitechturemovieapp.presentation.adapter.MovieListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MovieListFragment : BaseFragment<FragmentMovieListBinding>() {

    private val viewModel: MovieListViewModel by viewModels()
    private var movieListAdapter: MovieListAdapter? = null


    override fun viewBindingLayout(): FragmentMovieListBinding =
        FragmentMovieListBinding.inflate(layoutInflater)


    override fun initializeView(savedInstanceState: Bundle?) {
       // setUpRecyclerView()
       // viewModel.getMovieList()
      //  initViewCollect()

        binding.btnNavigateToReelsFragment.setOnClickListener {
            // Navigate to the fragment using the Navigation Component
           /* val bundle =Bundle().apply {
                putString("key","value")
            }*/
            findNavController().navigate(R.id.action_movieListFragment_to_reelsFragment)

        }
    }

    private fun setUpRecyclerView() {
        movieListAdapter = MovieListAdapter()
        binding.movieListRV.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.movieListRV.adapter = movieListAdapter
    }

    private fun initViewCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { response ->
                when (response) {
                    is Resource.Loading -> {
                        Timber.tag("Response").d("Loading")
                    }

                    is Resource.Success -> {
                        Timber.tag("Response").e("Response Size = ${response.data.size}")
                        movieListAdapter?.submitList(response.data)
                    }

                    is Resource.Error -> {
                        binding.noData.visibility = View.VISIBLE
                        Timber.tag("Response").e(response.throwable.localizedMessage ?: "Error")
                    }

                    else -> {
                        binding.noData.visibility = View.VISIBLE
                        Timber.tag("Response").d("Unknown Error")
                    }
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MovieListFragment()

    }

}