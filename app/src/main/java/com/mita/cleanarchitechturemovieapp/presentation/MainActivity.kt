package com.mita.cleanarchitechturemovieapp.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mita.cleanarchitechturemovieapp.R
import com.mita.cleanarchitechturemovieapp.common.baseComponent.BaseActivity
import com.mita.cleanarchitechturemovieapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun viewBindingLayout(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)


    override fun initializeView(savedInstanceState: Bundle?) {
        //TODO("Not yet implemented")
    }

}