package com.mita.cleanarchitechturemovieapp.presentation.reels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mita.cleanarchitechturemovieapp.data.model.DemoData
import com.mita.cleanarchitechturemovieapp.data.model.ReelsItem

class Reels2ViewModel : ViewModel() {
    private val _reels = MutableLiveData<List<ReelsItem>>()
    val reels: LiveData<List<ReelsItem>> get() = _reels

    init {
        loadDemoData()
    }

    private fun loadDemoData() {
        _reels.value= DemoData.reelsList
        /*_reels.value = listOf(
            Reel("1", "https://example.com/video1.mp4", 100, listOf("Great video!", "Loved it!"), 10),
            Reel("2", "https://example.com/video2.mp4", 150, listOf("Amazing!", "Wow!"), 20)
        )*/
    }

    fun likeReel(reelId: String) {
        _reels.value = _reels.value?.map {
            if (it.id == reelId) it.copy(likesCount = it.likesCount + 1) else it
        }
    }

    fun commentOnReel(reelId: String, ) {
        _reels.value = _reels.value?.map {
            if (it.id == reelId) it.copy(commentsCount = it.commentsCount + 1) else it
        }
    }

    /*fun shareReel(reelId: String) {
        _reels.value = _reels.value?.map {
            if (it.id == reelId) it.copy(shares = it.shares + 1) else it
        }
    }*/
}