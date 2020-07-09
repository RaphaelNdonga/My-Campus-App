package com.example.android.mycampusapp.timetable.days.monday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.EventObserver
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.databinding.FragmentMondayBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.timetable.TimetableFragmentDirections
import com.example.android.mycampusapp.util.TimePickerValues
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MondayFragment : Fragment() {
    @TimetableDatabase
    @Inject
    lateinit var repository: TimetableDataSource

    private val viewModel by viewModels<MondayViewModel> {
        MondayViewModelFactory(repository)
    }
    private lateinit var tracker: SelectionTracker<Long>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentMondayBinding>(
            inflater,
            R.layout.fragment_monday,
            container,
            false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val recyclerView = binding.mondayRecyclerView
        val adapter = MondayAdapter(MondayListener {
            viewModel.displayMondayClassDetails(it)
        })
        recyclerView.adapter = adapter
        tracker = SelectionTracker.Builder(
            "mondaySelection",
            recyclerView,
            MyItemKeyProvider(recyclerView),
            MyItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
        adapter.tracker = tracker

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                }
            })
        viewModel.addNewClass.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                TimetableFragmentDirections.actionTimetableFragmentToClassInputFragment()
            )
        })

        viewModel.openMondayClass.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                TimetableFragmentDirections.actionTimetableFragmentToClassInputFragment(it)
            )
        })

        return binding.root
    }
}