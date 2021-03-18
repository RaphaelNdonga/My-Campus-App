package com.example.android.mycampusapp.timetable.display.days.thursday

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.example.android.mycampusapp.R
import com.example.android.mycampusapp.data.TimetableClass
import com.example.android.mycampusapp.databinding.FragmentTimetableDisplayBinding
import com.example.android.mycampusapp.timetable.display.*
import com.example.android.mycampusapp.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ThursdayFragment : Fragment() {
    private lateinit var snapshotListener: ListenerRegistration

    @Inject
    lateinit var courseCollection: CollectionReference

    private lateinit var viewModel: TimetableViewModel
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var adapter: TimetableAdapter
    private lateinit var recyclerView: RecyclerView
    private var highlightState: Boolean = false
    private var isAdmin: Boolean = false
    private lateinit var courseId: String
    private lateinit var sharedPreferences: SharedPreferences
    private val thursday = DayOfWeek.THURSDAY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences =
            requireActivity().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        courseId = sharedPreferences.getString(COURSE_ID, "")!!
        isAdmin = sharedPreferences.getBoolean(IS_ADMIN, false)
        val binding = DataBindingUtil.inflate<FragmentTimetableDisplayBinding>(
            inflater,
            R.layout.fragment_timetable_display,
            container,
            false
        )
        Timber.i("thursday fragment created")

        val fab = binding.timetableFab
        if (isAdmin && !highlightState) {
            fab.visibility = View.VISIBLE
        }
        setHasOptionsMenu(true)
        val app = requireActivity().application
        viewModel = ViewModelProvider(
            this,
            TimetableViewModelFactory(
                courseCollection.document(courseId).collection(thursday.name),
                app
            )
        ).get(TimetableViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        recyclerView = binding.timetableRecyclerView
        adapter =
            TimetableAdapter(
                TimetableListener {
                    if (isAdmin) {
                        viewModel.displayFridayClassDetails(it)
                    }
                })
        recyclerView.adapter = adapter


        binding.timetableRefreshLayout.setOnRefreshListener {
            snapshotListener.remove()
            viewModel.addSnapshotListener()
            binding.timetableRefreshLayout.isRefreshing = false
        }

        viewModel.addNewClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToThursdayInputFragment()
                )
                Timber.i("We are supposed to be navigating")
            })

        viewModel.openFridayClass.observe(viewLifecycleOwner,
            EventObserver {
                findNavController().navigate(
                    TimetableFragmentDirections.actionTimetableFragmentToThursdayInputFragment(it)
                )
            })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupTracker()
        snapshotListener = viewModel.addSnapshotListener()
    }

    override fun onPause() {
        super.onPause()
        snapshotListener.remove()
    }

    private fun deleteSelectedItems(selection: Selection<Long>) {
        val list: List<TimetableClass?> = selection.map {
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
        inflater.inflate(R.menu.main_toolbar_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.delete_all_classes)
        item.isEnabled = highlightState && isAdmin
        item.isVisible = highlightState && isAdmin
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "thursdaySelection",
            recyclerView,
            MyItemKeyProvider(
                recyclerView
            ),
            TimetableItemDetailsLookup(
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