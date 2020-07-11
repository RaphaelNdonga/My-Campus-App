package com.example.android.mycampusapp.timetable.days.monday

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.Event
import com.example.android.mycampusapp.EventObserver
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.MondayClass
import com.example.android.mycampusapp.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.databinding.FragmentMondayBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.timetable.TimetableFragmentDirections
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var adapter: MondayAdapter
    private lateinit var recyclerView: RecyclerView

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

        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.mondayRecyclerView
        adapter = MondayAdapter(MondayListener {
            viewModel.displayMondayClassDetails(it)
        })
        recyclerView.adapter = adapter


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
        setupTracker()
        return binding.root
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<MondayClass?> = selection.map {
            adapter.currentList[it.toInt()]
        }.toList()
        viewModel.deleteList(list)
        tracker.selection.removeAll { true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_classes -> {
                viewModel.deleteIconPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_all_menu, menu)
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "mondaySelection",
            recyclerView,
            MyItemKeyProvider(recyclerView),
            MyItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val nItems: Int? = tracker.selection.size()
                    if (nItems != null)
                        viewModel.deleteMondayClasses.observe(viewLifecycleOwner, EventObserver {
                            deleteSelectedItems(tracker.selection)
                        })
                }
            })
        adapter.tracker = tracker
    }
}