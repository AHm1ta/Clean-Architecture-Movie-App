package com.mita.cleanarchitechturemovieapp.presentation.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.data.model.MovieItem
import com.mita.cleanarchitechturemovieapp.databinding.RowMovieItemBinding
import timber.log.Timber

class MovieListAdapter : ListAdapter<MovieItem, MovieListAdapter.ExampleItemViewHolder>(ExampleItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleItemViewHolder {
        val binding = RowMovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExampleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExampleItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ExampleItemViewHolder(private val binding: RowMovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movieItem: MovieItem) {
            binding.movieName.text = movieItem.title

            if (movieItem.image.isNotBlank()) {
                binding.movieImage.load(movieItem.image) {
                    placeholder(R.drawable.default_image)

                    listener(
                        onSuccess = { _, _ ->
                            Timber.tag("imageResponse").d(  "Success image Url = ${movieItem.image}")
                        },
                        onError = { request: ImageRequest, error: ErrorResult ->
                            request.error
                            Timber.tag("imageResponse").d( "Exception image Url = ${movieItem.image}  Error $error")
                            binding.movieImage.load(R.drawable.default_image)
                        }
                    )
                }
            } else {
                binding.movieImage.load(R.drawable.default_image)
            }
        }
    }

    class ExampleItemDiffCallback : DiffUtil.ItemCallback<MovieItem>() {
        override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
            return oldItem == newItem
        }
    }
}
