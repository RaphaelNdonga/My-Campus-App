package com.example.android.mycampusapp.timetable.display.days.friday

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.databinding.FragmentFridayBinding
import com.example.android.mycampusapp.di.TimetableDatabase
import com.example.android.mycampusapp.timetable.data.FridayClass
import com.example.android.mycampusapp.timetable.data.timetable.local.TimetableDataSource
import com.example.android.mycampusapp.timetable.display.MyItemKeyProvider
import com.example.android.mycampusapp.timetable.display.TimetableFragmentDirections
import com.example.android.mycampusapp.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FridayFragment : Fragment() {
    @TimetableDatabase
    @Inject
    lateinit var repository: TimetableDataSource

    private val viewModel by viewModels<FridayViewModel> {
        FridayViewModelFactory(
            repository
        )
    }
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: FridayAdapter
    private lateinit var recyclerView: RecyclerView
    private var highlightState: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFridayBinding>(
            inflater,
            R.layout.fragment_friday,
            container,
            false
        )
        Timber.i("friday fragment created")

        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.fridayRecyclerView
        adapter =
            FridayAdapter(
                FridayListener {
                    viewModel.displayFridayClassDetails(it)
                })
        recyclerView.adapter = adapter


        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToFridayInputFragment()
                )
            })

        viewModel.openFridayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToFridayInputFragment(it)
                )
            })
        setupTracker()
        return binding.root
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<FridayClass?> = selection.map {
            adapter.currentList[it.toInt()]
        }.toList()
        viewModel.deleteList(list)
        tracker.selection.removeAll { true }
        highlightState = false
        requireActivity().invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_classes -> {
                showAlertDialogBox()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.delete_all_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isEnabled = highlightState
        item.isVisible = highlightState
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "fridaySelection",
            recyclerView,
            MyItemKeyProvider(
                recyclerView
            ),
            FridayItemDetailsLookup(
                recyclerView
            ),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    highlightState = true
                    val nItems: Int? = tracker.selection.size()
                    if (nItems != null)
                        viewModel.deleteFridayClasses.observe(viewLifecycleOwner,
                            EventObserver {
                                deleteSelectedItems(tracker.selection)
                            })
                    if (nItems == 0) {
                        highlightState = false
                    }
                    requireActivity().invalidateOptionsMenu()
                }

            })
        adapter.tracker = tracker
    }

    private fun showAlertDialogBox() {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyCampusApp_Dialog)
        builder.setTitle(getString(R.string.dialog_delete))
        builder.setMessage(getString(R.string.dialog_delete_confirm))

        builder.setPositiveButton(getString(R.string.dialog_positive)) { _, _ -> viewModel.deleteIconPressed() }
        builder.setNegativeButton(getString(R.string.dialog_negative)) { _, _ -> }

        builder.create().show()
    }
}