package com.mita.cleanarchitechturemovieapp.common.baseComponent


import android.os.Bundle
import android.os.Handler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<V : ViewBinding> : Fragment() {

    private var _binding: V? = null
    val binding get() = _binding!!
    protected abstract fun viewBindingLayout(): V
    protected abstract fun initializeView(savedInstanceState: Bundle?)
    private var handler: Handler? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = viewBindingLayout()
        initializeView(savedInstanceState)
        return binding.root
    }

    protected fun showLongToast(msg: String?) {
        val context = context ?: return
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    }

    protected fun showShortToast(msg: String?) {
        val context = context ?: return
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler!!.removeCallbacksAndMessages(null)
        _binding = null
    }


    protected fun onBackPressed() {
        val view = view
        if (view != null) {
            Navigation.findNavController(view).navigateUp()
        }
    }
}