package com.mita.cleanarchitechturemovieapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem

class ReelsViewModel : ViewModel() {
    private val _reels = MutableLiveData<List<ReelsItem>>()
    val reels: LiveData<List<ReelsItem>> get() = _reels

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    fun setReels(list: List<ReelsItem>) {
        _reels.value = list
    }

    fun updateLikeStatus(position: Int, isLiked: Boolean) {
        _reels.value?.let { list ->
            val updatedReels = list.toMutableList()
            val reel = updatedReels[position]
            updatedReels[position] = reel.copy(
                likesCount = if (isLiked) reel.likesCount + 1 else reel.likesCount - 1
            )
            _reels.value = updatedReels
        }
    }

    fun setCurrentPosition(position: Int) {
        _currentPosition.value = position
    }
}