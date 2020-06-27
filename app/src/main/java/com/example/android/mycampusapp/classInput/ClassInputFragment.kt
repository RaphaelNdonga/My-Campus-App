package com.example.android.mycampusapp.classInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.timetable.local.TimetableDao
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.data.timetable.local.TimetableLocalDataSource
import com.example.android.mycampusapp.databinding.FragmentClassInputBinding
import com.example.android.mycampusapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar




class ClassInputFragment : Fragment() {

    lateinit var timetableRepository: TimetableDataSource

    private val viewModel by viewModels<ClassInputViewModel> {
        ClassInputViewModelFactory(timetableRepository as TimetableLocalDataSource)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentClassInputBinding>(
            inflater,
            R.layout.fragment_class_input,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSnackbar()
    }

    private fun setupSnackbar(){
        view?.setupSnackbar(this,viewModel.snackbarText,Snackbar.LENGTH_SHORT)
    }
}