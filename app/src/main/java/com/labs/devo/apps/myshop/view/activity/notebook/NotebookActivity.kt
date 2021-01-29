package com.labs.devo.apps.myshop.view.activity.notebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.labs.devo.apps.myshop.R
import com.labs.devo.apps.myshop.databinding.ActivityNotebookBinding
import com.labs.devo.apps.myshop.view.util.DataState
import com.labs.devo.apps.myshop.view.util.DataStateListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotebookActivity : AppCompatActivity(), DataStateListener {

    private lateinit var binding: ActivityNotebookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notebook)

        supportActionBar?.hide()
    }

    companion object {
        const val NO_NOTEBOOK_MSG = "No notebooks"
    }

    override fun onDataStateChange(dataState: DataState<*>?){
        dataState?.let{
            // Handle loading
            showProgressBar(dataState.loading)

            // Handle Message
            dataState.message?.let{ event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    /**
     * Show toast with the msg.
     */
    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Show progress bar.
     */
    private fun showProgressBar(isVisible: Boolean){
        if(isVisible){
            binding.notebookProgressBar.visibility = View.VISIBLE
        }
        else{
            binding.notebookProgressBar.visibility = View.INVISIBLE
        }
    }

}