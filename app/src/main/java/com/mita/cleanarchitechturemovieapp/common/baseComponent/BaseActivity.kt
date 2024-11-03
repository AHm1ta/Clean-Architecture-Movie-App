package com.mita.cleanarchitechturemovieapp.common.baseComponent

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<D: ViewBinding> : AppCompatActivity(){

    lateinit var binding:D
    private var activityContext: Activity? = null
    protected abstract fun viewBindingLayout(): D
    protected abstract fun initializeView(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewBindingLayout()
        setContentView(binding.root)
        activityContext = this
        initializeView(savedInstanceState)
    }
}